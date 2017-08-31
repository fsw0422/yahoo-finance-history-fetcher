import akka.http.scaladsl.model.DateTime
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class FetcherSpec extends FlatSpec with Matchers {

  // inject instances
  val fetcher = App.globalInjector.getInstance(classOf[FetcherImpl])

  "#getSession" should "give back HTTP OK response\n" in {
    // argument
    val ticker = "FB"

    // process
    val response = Await.result(fetcher.getSession(ticker), 100 seconds)

    // assert
    response.status.intValue should be(200)
  }

  "#getStockHistory" should "give back HTTP OK response\n" in {
    // argument
    val ticker = "FB"
    val startDate = 0L
    val endDate = DateTime.now.clicks
    val interval = "1d"

    // process
    val response = Await.result(fetcher.getStockHistory(ticker, startDate, endDate, interval), 100 seconds)

    // assert
    response.status.intValue should be(200)
  }
}
