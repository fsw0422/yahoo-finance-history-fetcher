import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class StockFetcherSpec extends FlatSpec with Matchers {

  private final implicit val actorSystem = ActorSystem("Test")
  private final implicit val executionContext = actorSystem.dispatcher
  private final implicit val actorMaterializerSettings = ActorMaterializerSettings(actorSystem)
  private final implicit val actorMaterializer = ActorMaterializer(actorMaterializerSettings)(actorSystem)
  private final implicit val standaloneAhcWSClient = StandaloneAhcWSClient()

  behavior of "StockFetcher"

  it should "return historical records from Yahoo Finance when getStockHistory is invoked" in {
    val stockFetcher = StockFetcher()

    val ticker = "FB"
    val startDate = 0L
    val endDate = 1563226210
    val interval = "1d"

    val stockDaos = stockFetcher
      .getStockHistory(ticker, startDate, endDate, interval)
      .unsafeRunSync()

    stockDaos.size should be > 0
  }
}
