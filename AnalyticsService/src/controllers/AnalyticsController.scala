package controllers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, entity, path}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.ContentTypes
import services.AnalyticsService
import domain.Analytics
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

class AnalyticsController(analyticsService: AnalyticsService)(implicit ec: ExecutionContext) {

  // JSON format for Analytics case class
  implicit val analyticsFormat = jsonFormat3(Analytics)

  val route: Route =
    pathPrefix("analytics") {
      concat(
        path("log") {
          post {
            entity(as[Analytics]) { analyticsData =>
              onSuccess(analyticsService.logAnalytics(analyticsData)) { result =>
                complete(HttpResponse(StatusCodes.OK, entity = "Analytics data logged successfully"))
              }
            }
          }
        },
        path("user" / Segment) { userId =>
          get {
            onSuccess(analyticsService.getUserAnalytics(userId)) { result =>
              complete(HttpResponse(StatusCodes.OK, entity = result))
            }
          }
        },
        path("video" / Segment) { videoId =>
          get {
            onSuccess(analyticsService.getVideoAnalytics(videoId)) { result =>
              complete(HttpResponse(StatusCodes.OK, entity = result))
            }
          }
        },
        path("trends") {
          get {
            onSuccess(analyticsService.getTrendingVideos()) { result =>
              complete(HttpResponse(StatusCodes.OK, entity = result))
            }
          }
        }
      )
    }
}

// AnalyticsService trait defining service contract
package services

import domain.Analytics
import scala.concurrent.Future

trait AnalyticsService {
  def logAnalytics(data: Analytics): Future[String]
  def getUserAnalytics(userId: String): Future[String]
  def getVideoAnalytics(videoId: String): Future[String]
  def getTrendingVideos(): Future[String]
}

// AnalyticsService implementation
package services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import domain.{Analytics, AnalyticsRepository}

class AnalyticsServiceImpl(repository: AnalyticsRepository)(implicit ec: ExecutionContext) extends AnalyticsService {

  override def logAnalytics(data: Analytics): Future[String] = {
    repository.saveAnalytics(data).map { _ =>
      "Analytics data logged successfully"
    }
  }

  override def getUserAnalytics(userId: String): Future[String] = {
    repository.findAnalyticsByUser(userId).map { analytics =>
      s"User analytics: $analytics"
    }
  }

  override def getVideoAnalytics(videoId: String): Future[String] = {
    repository.findAnalyticsByVideo(videoId).map { analytics =>
      s"Video analytics: $analytics"
    }
  }

  override def getTrendingVideos(): Future[String] = {
    repository.findTrendingVideos().map { analytics =>
      s"Trending videos: $analytics"
    }
  }
}

// Analytics case class representing analytics data
package domain

case class Analytics(userId: String, videoId: String, timestamp: Long)

// AnalyticsRepository trait for data access layer
package domain

import scala.concurrent.Future

trait AnalyticsRepository {
  def saveAnalytics(data: Analytics): Future[Unit]
  def findAnalyticsByUser(userId: String): Future[List[Analytics]]
  def findAnalyticsByVideo(videoId: String): Future[List[Analytics]]
  def findTrendingVideos(): Future[List[Analytics]]
}

// Implementation of AnalyticsRepository
package domain

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.ConcurrentHashMap

class AnalyticsRepositoryImpl(implicit ec: ExecutionContext) extends AnalyticsRepository {

  private val analyticsData = new ConcurrentHashMap[String, Analytics]()

  override def saveAnalytics(data: Analytics): Future[Unit] = Future {
    analyticsData.put(data.userId + data.videoId, data)
  }

  override def findAnalyticsByUser(userId: String): Future[List[Analytics]] = Future {
    analyticsData.values().toArray.toList.collect {
      case a: Analytics if a.userId == userId => a
    }
  }

  override def findAnalyticsByVideo(videoId: String): Future[List[Analytics]] = Future {
    analyticsData.values().toArray.toList.collect {
      case a: Analytics if a.videoId == videoId => a
    }
  }

  override def findTrendingVideos(): Future[List[Analytics]] = Future {
    analyticsData.values().toArray.toList
  }
}

// Main Application to start the server
package app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import controllers.AnalyticsController
import services.{AnalyticsServiceImpl, AnalyticsService}
import domain.{AnalyticsRepository, AnalyticsRepositoryImpl}

import scala.concurrent.ExecutionContext

object AnalyticsApp extends App {
  implicit val system: ActorSystem = ActorSystem("AnalyticsSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val repository: AnalyticsRepository = new AnalyticsRepositoryImpl()
  val analyticsService: AnalyticsService = new AnalyticsServiceImpl(repository)
  val analyticsController = new AnalyticsController(analyticsService)

  Http().bindAndHandle(analyticsController.route, "localhost", 8080)
}