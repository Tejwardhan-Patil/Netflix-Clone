package services

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import domain.Analytics
import repositories.AnalyticsRepository
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContext

case class AnalyticsRequest(userId: String, videoId: String, action: String, timestamp: Long)
case class AnalyticsResponse(status: String, message: String)

trait AnalyticsJsonSupport extends DefaultJsonProtocol {
  implicit val analyticsRequestFormat = jsonFormat4(AnalyticsRequest)
  implicit val analyticsResponseFormat = jsonFormat2(AnalyticsResponse)
}

class AnalyticsService(analyticsRepository: AnalyticsRepository)
                      (implicit system: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext) extends AnalyticsJsonSupport {

  val logger = Logging(system, getClass)

  implicit val timeout: Timeout = Timeout(5.seconds)

  // Route definition
  val routes = pathPrefix("analytics") {
    logRequestResult("analytics-service") {
      post {
        entity(as[AnalyticsRequest]) { request =>
          val result = processAnalyticsData(request)
          onComplete(result) {
            case Success(response) => complete(response)
            case Failure(ex) =>
              logger.error(s"Failed to process analytics request: ${ex.getMessage}")
              complete(HttpResponse(StatusCodes.InternalServerError, entity = "Internal server error"))
          }
        }
      }
    }
  }

  // Function to process analytics data
  def processAnalyticsData(request: AnalyticsRequest): Future[AnalyticsResponse] = {
    logger.info(s"Processing analytics for user: ${request.userId}, video: ${request.videoId}, action: ${request.action}")
    
    val analytics = Analytics(request.userId, request.videoId, request.action, request.timestamp)
    analyticsRepository.saveAnalyticsData(analytics).map { result =>
      if (result) {
        AnalyticsResponse("success", "Analytics data processed successfully")
      } else {
        AnalyticsResponse("failure", "Failed to process analytics data")
      }
    }
  }

  // Function to fetch analytics data for reporting
  def getAnalyticsData(userId: String, videoId: Option[String]): Future[Seq[Analytics]] = {
    logger.info(s"Fetching analytics for user: $userId")
    analyticsRepository.getAnalyticsByUser(userId, videoId)
  }

  // Function to start Akka HTTP server
  def startServer(address: String, port: Int): Future[Http.ServerBinding] = {
    Http().bindAndHandle(routes, address, port).map { binding =>
      logger.info(s"AnalyticsService started on ${binding.localAddress}")
      binding
    }.recover {
      case ex =>
        logger.error(s"Failed to start AnalyticsService: ${ex.getMessage}")
        throw ex
    }
  }
}

object AnalyticsServiceApp extends App {
  implicit val system: ActorSystem = ActorSystem("analytics-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val analyticsRepository = new AnalyticsRepository() 
  val analyticsService = new AnalyticsService(analyticsRepository)

  val address = "0.0.0.0"
  val port = 8080

  analyticsService.startServer(address, port).onComplete {
    case Success(binding) => println(s"Server started at ${binding.localAddress}")
    case Failure(ex)      => println(s"Failed to start server: ${ex.getMessage}")
  }
}