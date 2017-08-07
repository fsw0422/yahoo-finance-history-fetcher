import akka.actor.ActorSystem
import akka.http.scaladsl.model.DateTime
import com.google.inject.Guice
import org.specs2.mutable._

import scala.concurrent.Await
import scala.concurrent.duration._

class FetcherSpec extends Specification {

  // injector
  val injector = Guice.createInjector(
    ConfigModule("resources/app.conf"),
    ActorSystemModule(ActorSystem("GLOBAL")),
    AkkaHttpUtilsModule(),
    FetcherModule()
  )

  // inject instances
  val fetcher = injector.getInstance(classOf[FetcherImpl])

  "#getSession" should {

    "give back HTTP OK response\n" in {
      // argument
      val ticker = "FB"

      // process
      val response = Await.result(fetcher.getSession(ticker), 100 seconds)

      // assert
      response.status.intValue mustEqual 200
    }
  }

  "#getStockHistory" should {

    "give back HTTP OK response\n" in {
      // argument
      val ticker = "FB"
      val startDate = 0L
      val endDate = DateTime.now.clicks
      val interval = "1d"

      // process
      val response = Await.result(fetcher.getStockHistory(ticker, startDate, endDate, interval), 100 seconds)

      // assert
      response.status.intValue mustEqual 200
    }
  }
}
