import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import org.joda.time.DateTime
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import scala.concurrent.duration._

import scala.concurrent.Await

class StockSpec extends PropSpec with PropertyChecks with Matchers {
  implicit val actorSystem = ActorSystem("Test")
  implicit val executionContext = actorSystem.dispatcher
  implicit val actorMaterializerSettings = ActorMaterializerSettings(
    actorSystem
  )
  implicit val actorMaterializer =
    ActorMaterializer(actorMaterializerSettings)(actorSystem)
  implicit val standaloneAhcWSClient = StandaloneAhcWSClient()

  property("getStockHistory") {
    val stockDao = Stock()

    val ticker = "FB"
    val startDate = 0L
    val endDate = DateTime.now.getMillis
    val interval = "1d"

    stockDao
      .getStockHistory(ticker, startDate, endDate, interval)
      .unsafeRunSync()
      .map(stockDfs => stockDfs.size should be > 0)
  }
}
