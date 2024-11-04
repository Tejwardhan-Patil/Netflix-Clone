package producers

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.SendProducer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// Case class representing the user action event
case class UserActionEvent(userId: String, action: String, timestamp: Long)

// Event serialization utility
object EventSerializer {
  def serialize(event: UserActionEvent): String = {
    s"${event.userId},${event.action},${event.timestamp}"
  }
}

// Kafka producer configuration
object KafkaConfig {
  val kafkaBootstrapServers = "localhost:9092"
  val kafkaTopic = "user-actions"

  def producerSettings(implicit system: ActorSystem) = {
    ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(kafkaBootstrapServers)
  }
}

// UserActionProducer class
class UserActionProducer(implicit system: ActorSystem, ec: ExecutionContext) {
  
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val producerSettings = KafkaConfig.producerSettings
  private val producer = SendProducer(producerSettings)
  
  // Flow for processing and publishing user action events
  private val userActionFlow: Flow[UserActionEvent, Future[ProducerRecord[Array[Byte], String]], _] = 
    Flow[UserActionEvent].map { event =>
      val serializedEvent = EventSerializer.serialize(event)
      Future {
        new ProducerRecord(KafkaConfig.kafkaTopic, serializedEvent)
      }
    }

  // Sink to send user action events to Kafka
  private val kafkaSink: Sink[Future[ProducerRecord[Array[Byte], String]], _] = 
    Sink.foreach[Future[ProducerRecord[Array[Byte], String]]] { recordFuture =>
      recordFuture.onComplete {
        case Success(record) => producer.send(record)
        case Failure(exception) => 
          println(s"Failed to send record to Kafka: ${exception.getMessage}")
      }
    }

  // Source of user action events
  def sendUserActions(userActions: Seq[UserActionEvent]): Unit = {
    Source(userActions)
      .via(userActionFlow)
      .to(kafkaSink)
      .run()
  }
}

// Companion object for initializing and running the producer
object UserActionProducerApp extends App {
  implicit val system: ActorSystem = ActorSystem("UserActionProducerSystem")
  implicit val ec: ExecutionContext = system.dispatcher

  val userActions = Seq(
    UserActionEvent("user1", "login", System.currentTimeMillis()),
    UserActionEvent("user2", "watch_video", System.currentTimeMillis()),
    UserActionEvent("user3", "logout", System.currentTimeMillis())
  )

  val producer = new UserActionProducer()
  producer.sendUserActions(userActions)

  sys.addShutdownHook {
    system.terminate()
  }
}