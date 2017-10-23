import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.Materializer
import com.google.inject.{AbstractModule, Inject, Singleton}
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait Fetcher {

  /**
    * initializes session that is needed for further requests
    *
    * @param ticker the symbol of stock
    * @return
    */
  def getSession(ticker: String): Future[HttpResponse]

  /**
    * retrieves the CSV from Yahoo finance and convert to JSON object list
    *
    * @param ticker    the symbol of stock
    * @param startDate the start date in epoch
    * @param endDate   the end date in epoch
    * @param interval  should be one of 1d, 1w, 1y
    * @return
    */
  def getStockHistory(ticker: String, startDate: Long, endDate: Long, interval: String): Future[HttpResponse]
}

@Singleton
case class FetcherImpl @Inject()
(akkaHttpUtils: AkkaHttpClientUtils)
(implicit config: Config,
 actorSystem: ActorSystem,
 materializer: Materializer,
 executionContext: ExecutionContext) extends Fetcher {

  override def getSession(ticker: String): Future[HttpResponse] = {
    val url = config.getString("url.getSession")
    Http().singleRequest(HttpRequest(
      method = HttpMethods.GET,
      uri = String.format(url, ticker)
    ).withHeaders(RawHeader("charset", "utf-8")))
  }

  override def getStockHistory(ticker: String, startDate: Long, endDate: Long, interval: String):
  Future[HttpResponse] = {

    /**
      * parses crumb from initial session response HTTP body
      *
      * @param body the HTTP body of the response
      * @return
      */
    def getCrumb(body: String): String = {
      """"CrumbStore":\{"crumb":"([^"]+)"\}""".r("crumb").findFirstMatchIn(body).get
                                              .group("crumb")
                                              .replaceAll("\\u002F", "/")
    }

    val url = config.getString("url.getStockHistory")
    val sessionResponse = Await.result(getSession(ticker), 20 seconds)
    val cookies = akkaHttpUtils.getCookies(sessionResponse)
    val body = akkaHttpUtils.getBody(sessionResponse, 20 seconds)
    val crumb = getCrumb(body)
    Http().singleRequest(HttpRequest(
      method = HttpMethods.GET,
      uri = String.format(url, ticker, startDate.toString, endDate.toString, interval, crumb)
    ).withHeaders(RawHeader("cookie", cookies.head)))
  }
}

case class FetcherModule() extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Fetcher]).to(classOf[FetcherImpl])
  }
}
