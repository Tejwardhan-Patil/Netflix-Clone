package repositories

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.{ExecutionContext, Future}
import domain.Analytics

class AnalyticsRepository(implicit system: ActorSystem, materializer: Materializer, ec: ExecutionContext) {

  private val httpClient = Http(system)

  def getAnalyticsDataByUserId(userId: String): Future[Option[Analytics]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"http://website.com/api/analytics/$userId"
    )

    for {
      response <- httpClient.singleRequest(request)
      analytics <- handleResponse(response)
    } yield analytics
  }

  def getAllAnalyticsData(): Future[List[Analytics]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = "http://website.com/api/analytics"
    )

    for {
      response <- httpClient.singleRequest(request)
      analyticsList <- handleListResponse(response)
    } yield analyticsList
  }

  def saveAnalyticsData(analytics: Analytics): Future[Unit] = {
    val entity = HttpEntity(
      ContentTypes.`application/json`,
      analytics.toJson.toString
    )

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://website.com/api/analytics",
      entity = entity
    )

    for {
      response <- httpClient.singleRequest(request)
      _ <- handlePostResponse(response)
    } yield ()
  }

  def updateAnalyticsData(userId: String, analytics: Analytics): Future[Unit] = {
    val entity = HttpEntity(
      ContentTypes.`application/json`,
      analytics.toJson.toString
    )

    val request = HttpRequest(
      method = HttpMethods.PUT,
      uri = s"http://website.com/api/analytics/$userId",
      entity = entity
    )

    for {
      response <- httpClient.singleRequest(request)
      _ <- handlePostResponse(response)
    } yield ()
  }

  def deleteAnalyticsData(userId: String): Future[Unit] = {
    val request = HttpRequest(
      method = HttpMethods.DELETE,
      uri = s"http://website.com/api/analytics/$userId"
    )

    for {
      response <- httpClient.singleRequest(request)
      _ <- handleDeleteResponse(response)
    } yield ()
  }

  private def handleResponse(response: HttpResponse): Future[Option[Analytics]] = {
    response.status match {
      case StatusCodes.OK => Unmarshal(response.entity).to[Analytics].map(Some(_))
      case StatusCodes.NotFound => Future.successful(None)
      case _ => Future.failed(new RuntimeException(s"Failed to fetch analytics data: ${response.status}"))
    }
  }

  private def handleListResponse(response: HttpResponse): Future[List[Analytics]] = {
    response.status match {
      case StatusCodes.OK => Unmarshal(response.entity).to[List[Analytics]]
      case _ => Future.failed(new RuntimeException(s"Failed to fetch analytics list: ${response.status}"))
    }
  }

  private def handlePostResponse(response: HttpResponse): Future[Unit] = {
    response.status match {
      case StatusCodes.Created | StatusCodes.OK => Future.successful(())
      case _ => Future.failed(new RuntimeException(s"Failed to save analytics data: ${response.status}"))
    }
  }

  private def handleDeleteResponse(response: HttpResponse): Future[Unit] = {
    response.status match {
      case StatusCodes.OK | StatusCodes.NoContent => Future.successful(())
      case _ => Future.failed(new RuntimeException(s"Failed to delete analytics data: ${response.status}"))
    }
  }
}