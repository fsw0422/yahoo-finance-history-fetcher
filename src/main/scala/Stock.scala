import akka.actor.ActorSystem
import akka.stream.Materializer
import cats.effect.IO
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.language.higherKinds
import scala.concurrent.{ExecutionContext, Future}

case class StockDf(date: String,
                   open: Double,
                   high: Double,
                   low: Double,
                   close: Double,
                   adjClose: Double,
                   volume: Long)

object StockDf {

  def mapDataToDf(data: String): Seq[StockDf] = {
    for {
      line <- data.split("\n").drop(1)
      stockDf = {
        val words = line.split(",")
        StockDf(
          date = words(0),
          open = words(1).toDouble,
          high = words(2).toDouble,
          low = words(3).toDouble,
          close = words(4).toDouble,
          adjClose = words(5).toDouble,
          volume = words(6).toLong
        )
      }
    } yield stockDf
  }
}

case class Stock(implicit actorSystem: ActorSystem,
                 materializer: Materializer,
                 executionContext: ExecutionContext,
                 standaloneAhcWSClient: StandaloneAhcWSClient) {

  def getStockHistory(ticker: String,
                      startDate: Long,
                      endDate: Long,
                      interval: String): IO[Future[Seq[StockDf]]] = {
    IO.pure {
      for {
        session <- standaloneAhcWSClient
          .url(
            String.format("https://finance.yahoo.com/quote/%s/history", ticker)
          )
          .addHttpHeaders(("charset", "utf-8"))
          .get
        cookies = session.cookies
        crumb = """"CrumbStore":\{"crumb":"([^"]+)"\}"""
          .r("crumb")
          .findFirstMatchIn(session.body)
          .get
          .group("crumb")
          .replaceAll("\\u002F", "/")
        data <- standaloneAhcWSClient
          .url(
            String.format(
              "https://query1.finance.yahoo.com/v7/finance/download/%s?period1=%s&period2=%s&interval=%s&events=history&crumb=%s",
              ticker,
              startDate.toString,
              endDate.toString,
              interval,
              crumb
            )
          )
          .addHttpHeaders(("charset", "utf-8"))
          .addCookies(cookies.head)
          .get
          .map(response => StockDf.mapDataToDf(response.body))
      } yield data
    }
  }
}
