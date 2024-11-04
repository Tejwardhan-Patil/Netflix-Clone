package consumers

import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.ConsumerMessage
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import akka.kafka.scaladsl.{Committer, CommitterSettings}
import akka.kafka.{ConsumerSettings, Subscriptions}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import akka.Done
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.DrainingControl
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.errors.WakeupException
import spray.json._
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.Future
import java.util.Properties
import scala.collection.mutable

object RecommendationConsumer {
  def props: Props = Props(new RecommendationConsumer)
}

class RecommendationConsumer extends Actor with ActorLogging {
  implicit val ec: ExecutionContextExecutor = context.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()(context.system)
  
  val kafkaBootstrapServers = "localhost:9092"
  val groupId = "recommendation-consumer-group"
  val topic = "recommendation-topic"

  // In-memory recommendation storage
  val recommendationStorage: mutable.Map[String, List[Recommendation]] = mutable.Map()

  // Kafka Consumer settings
  val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(context.system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId(groupId)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  // Committing settings
  val committerSettings: CommitterSettings = CommitterSettings(context.system)

  // Starting the Kafka consumer
  override def preStart(): Unit = {
    log.info("Starting RecommendationConsumer Actor")

    // Kafka Source stream setup
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(1)(msg => processMessage(msg).map(_ => msg.committableOffset))
      .runWith(Committer.sink(committerSettings))
  }

  // Process incoming Kafka messages
  def processMessage(msg: ConsumerMessage.CommittableMessage[String, String]): Future[Unit] = {
    Future {
      log.info(s"Consumed message: ${msg.record.value()}")
      val recommendation = parseRecommendation(msg.record.value())

      recommendation match {
        case Some(rec) =>
          // Process the recommendation
          log.info(s"Processing recommendation for user: ${rec.userId}, videoId: ${rec.videoId}")
          storeRecommendation(rec)
        case None =>
          log.error(s"Failed to parse recommendation message: ${msg.record.value()}")
      }
    }
  }

  // Parse recommendation JSON
  def parseRecommendation(json: String): Option[Recommendation] = {
    try {
      val parsed = json.parseJson.convertTo[Recommendation]
      Some(parsed)
    } catch {
      case ex: Exception =>
        log.error(s"Error parsing JSON: ${ex.getMessage}")
        None
    }
  }

  // Store recommendation in in-memory map or replace with DB logic
  def storeRecommendation(rec: Recommendation): Unit = {
    val recommendations = recommendationStorage.getOrElse(rec.userId, List())
    recommendationStorage.update(rec.userId, recommendations :+ rec)
    log.info(s"Stored recommendation for userId: ${rec.userId}, total recommendations: ${recommendations.size + 1}")
  }

  // Fetch recommendations for a given user
  def getRecommendationsForUser(userId: String): List[Recommendation] = {
    recommendationStorage.getOrElse(userId, List())
  }

  // Shutdown consumer gracefully
  override def postStop(): Unit = {
    log.info("Shutting down RecommendationConsumer")
  }

  // Receive function to handle messages
  override def receive: Receive = {
    case FetchRecommendations(userId) =>
      val recommendations = getRecommendationsForUser(userId)
      sender() ! Recommendations(recommendations)
    case message =>
      log.warning(s"Unhandled message: $message")
  }
}

// Recommendation case class and JSON protocol
final case class Recommendation(userId: String, videoId: String, timestamp: Long)
final case class FetchRecommendations(userId: String)
final case class Recommendations(recommendations: List[Recommendation])

object RecommendationJsonProtocol extends DefaultJsonProtocol {
  implicit val recommendationFormat: RootJsonFormat[Recommendation] = jsonFormat3(Recommendation)
}

object KafkaConfig {
  def createConsumerProps(): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "recommendation-consumer-group")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props
  }
}

object RecommendationMain extends App {
  implicit val system: ActorSystem = ActorSystem("RecommendationConsumerSystem")
  val recommendationConsumer = system.actorOf(RecommendationConsumer.props, "recommendationConsumer")

  // Shutdown hook
  sys.addShutdownHook {
    system.terminate()
  }
}