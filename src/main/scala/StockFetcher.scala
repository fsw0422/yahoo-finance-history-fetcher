import akka.actor.ActorSystem
import cats.effect.IO
import play.api.libs.ws.WSCookie
import play.api.libs.ws.ahc.StandaloneAhcWSClient

case class StockFetcher(
  implicit actorSystem: ActorSystem,
  standaloneAhcWSClient: StandaloneAhcWSClient
) {
  private final implicit val dispatcher = actorSystem.dispatcher

  def getStockHistory(
    ticker: String,
    startDate: Long,
    endDate: Long,
    interval: String
  ): IO[Seq[StockDf]] = {
    for {
      session <- getSession(ticker)
      crumb <- IO(extractCrumb(session._2))
      stockHistory <- getData(session._1, ticker, startDate, endDate, interval, crumb)
    } yield stockHistory
  }

  private def getSession(ticker: String): IO[(Seq[WSCookie], String)] = IO.fromFuture {
    IO {
      standaloneAhcWSClient
        .url(s"https://finance.yahoo.com/quote/$ticker/history")
        .addHttpHeaders(("charset", "utf-8"))
        .get
        .map(resp => (resp.cookies, resp.body))
      }
  }

  private def extractCrumb(sessionBody: String): String = {
    """"CrumbStore":\{"crumb":"([^"]+)"\}"""
      .r("crumb")
      .findFirstMatchIn(sessionBody)
      .get
      .group("crumb")
      .replaceAll("\\u002F", "/")
  }

  private def getData(
    cookies: Seq[WSCookie],
    ticker: String,
    startDate: Long,
    endDate: Long,
    interval: String,
    crumb: String,
  ): IO[Seq[StockDf]] = IO.fromFuture {
    IO {
      standaloneAhcWSClient
        .url(s"https://query1.finance.yahoo.com/v7/finance/download/$ticker?period1=${startDate.toString}&period2=${endDate.toString}&interval=$interval&events=history&crumb=$crumb")
        .addHttpHeaders(("charset", "utf-8"))
        .addCookies(cookies.head)
        .get
        .map(response => StockDf.mapDataToDf(response.body))
    }
  }

}
