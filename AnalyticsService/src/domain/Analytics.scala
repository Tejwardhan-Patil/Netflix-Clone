package domain

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import spray.json._
import scala.concurrent.{ExecutionContext, Future}

// Case classes for Analytics data
case class UserActivity(userId: String, action: String, timestamp: Long, metadata: Map[String, String])
case class VideoPlayEvent(videoId: String, userId: String, duration: Long, timestamp: Long)
case class SearchQuery(userId: String, query: String, timestamp: Long)
case class AdClick(userId: String, adId: String, timestamp: Long, revenue: Double)

object AnalyticsJsonProtocol extends DefaultJsonProtocol {
  implicit val userActivityFormat = jsonFormat4(UserActivity)
  implicit val videoPlayEventFormat = jsonFormat4(VideoPlayEvent)
  implicit val searchQueryFormat = jsonFormat3(SearchQuery)
  implicit val adClickFormat = jsonFormat4(AdClick)
}

// Service to manage streaming analytics
class AnalyticsStreamService(implicit system: ActorSystem, ec: ExecutionContext, materializer: ActorMaterializer) {
  import AnalyticsJsonProtocol._

  // Sample source of raw analytics data
  def rawAnalyticsSource: Source[String, _] = Source(List(
    """{"userId": "user1", "action": "play_video", "timestamp": 1634320000, "metadata": {"videoId": "vid1"}}""",
    """{"userId": "user2", "action": "search", "timestamp": 1634320020, "metadata": {"query": "new movies"}}""",
    """{"userId": "user3", "action": "ad_click", "timestamp": 1634320050, "metadata": {"adId": "ad123", "revenue": "1.25"}}"""
  ))

  // Parse raw JSON to corresponding case class
  def parseAnalyticsData(jsonStr: String): Option[UserActivity] = {
    try {
      val json = jsonStr.parseJson.asJsObject
      val action = json.fields("action").convertTo[String]
      action match {
        case "play_video" =>
          val videoId = json.fields("metadata").asJsObject.fields("videoId").convertTo[String]
          Some(UserActivity(json.fields("userId").convertTo[String], action, json.fields("timestamp").convertTo[Long], Map("videoId" -> videoId)))
        case "search" =>
          val query = json.fields("metadata").asJsObject.fields("query").convertTo[String]
          Some(UserActivity(json.fields("userId").convertTo[String], action, json.fields("timestamp").convertTo[Long], Map("query" -> query)))
        case "ad_click" =>
          val adId = json.fields("metadata").asJsObject.fields("adId").convertTo[String]
          val revenue = json.fields("metadata").asJsObject.fields("revenue").convertTo[Double]
          Some(UserActivity(json.fields("userId").convertTo[String], action, json.fields("timestamp").convertTo[Long], Map("adId" -> adId, "revenue" -> revenue.toString)))
        case _ => None
      }
    } catch {
      case _: Exception => None
    }
  }

  // Flow to process incoming analytics data
  def analyticsFlow: Flow[String, Option[UserActivity], _] =
    Flow[String].map(parseAnalyticsData)

  // Sink to log parsed analytics data
  def analyticsSink: Sink[Option[UserActivity], Future[akka.Done]] =
    Sink.foreach {
      case Some(activity) => println(s"Parsed User Activity: $activity")
      case None           => println("Failed to parse activity")
    }

  // Main stream processing function
  def processAnalyticsStream(): Unit = {
    rawAnalyticsSource
      .via(analyticsFlow)
      .runWith(analyticsSink)
  }
}

// Analytics aggregation
class AnalyticsAggregator {

  private var videoPlays: Map[String, Long] = Map.empty // VideoID -> total play duration
  private var searchQueries: Map[String, Int] = Map.empty // UserID -> number of searches
  private var adClicks: Map[String, Double] = Map.empty // AdID -> total revenue

  // Aggregate play events
  def aggregateVideoPlay(event: VideoPlayEvent): Unit = {
    videoPlays = videoPlays + (event.videoId -> (videoPlays.getOrElse(event.videoId, 0L) + event.duration))
  }

  // Aggregate search queries
  def aggregateSearchQuery(query: SearchQuery): Unit = {
    searchQueries = searchQueries + (query.userId -> (searchQueries.getOrElse(query.userId, 0) + 1))
  }

  // Aggregate ad clicks
  def aggregateAdClick(click: AdClick): Unit = {
    adClicks = adClicks + (click.adId -> (adClicks.getOrElse(click.adId, 0.0) + click.revenue))
  }

  // Report current statistics
  def reportStats(): Unit = {
    println(s"Video Plays: $videoPlays")
    println(s"Search Queries: $searchQueries")
    println(s"Ad Clicks: $adClicks")
  }
}

// A repository layer to store the aggregated analytics
class AnalyticsRepository {

  private var videoPlayRecords: List[VideoPlayEvent] = List.empty
  private var searchQueryRecords: List[SearchQuery] = List.empty
  private var adClickRecords: List[AdClick] = List.empty

  // Save a video play event
  def saveVideoPlay(event: VideoPlayEvent): Unit = {
    videoPlayRecords = videoPlayRecords :+ event
    println(s"Saved video play event: $event")
  }

  // Save a search query
  def saveSearchQuery(query: SearchQuery): Unit = {
    searchQueryRecords = searchQueryRecords :+ query
    println(s"Saved search query: $query")
  }

  // Save an ad click event
  def saveAdClick(click: AdClick): Unit = {
    adClickRecords = adClickRecords :+ click
    println(s"Saved ad click event: $click")
  }

  // Fetch all video play records
  def fetchAllVideoPlays(): List[VideoPlayEvent] = videoPlayRecords

  // Fetch all search query records
  def fetchAllSearchQueries(): List[SearchQuery] = searchQueryRecords

  // Fetch all ad click records
  def fetchAllAdClicks(): List[AdClick] = adClickRecords
}

// Main application to run the analytics service
object AnalyticsApp extends App {
  implicit val system: ActorSystem = ActorSystem("AnalyticsSystem")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val analyticsService = new AnalyticsStreamService()
  val aggregator = new AnalyticsAggregator()
  val repository = new AnalyticsRepository()

  // Run the analytics stream processor
  analyticsService.processAnalyticsStream()

  // Simulate incoming events
  repository.saveVideoPlay(VideoPlayEvent("vid1", "user1", 1200, System.currentTimeMillis()))
  repository.saveSearchQuery(SearchQuery("user2", "new movies", System.currentTimeMillis()))
  repository.saveAdClick(AdClick("user3", "ad123", System.currentTimeMillis(), 1.25))

  // Report aggregated stats
  aggregator.reportStats()
}