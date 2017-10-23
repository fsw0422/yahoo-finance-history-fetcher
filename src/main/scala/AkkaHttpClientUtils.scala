import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import com.google.inject.{AbstractModule, Inject, Singleton}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

trait AkkaHttpClientUtils {

  /**
    *
    * @param httpResponse
    * @return
    */
  def getCookies(httpResponse: HttpResponse): List[String]

  /**
    *
    * @param httpResponse
    * @param awaitFor
    * @return
    */
  def getBody(httpResponse: HttpResponse, awaitFor: FiniteDuration): String
}

@Singleton
case class AkkaHttpClientUtilsImpl @Inject()
(implicit materializer: Materializer,
 executionContext: ExecutionContext) extends AkkaHttpClientUtils {

  override def getCookies(httpResponse: HttpResponse): List[String] = {
    httpResponse.headers
    .filter(_.name == "set-cookie")
    .map(_.value)
    .head
    .split("; ")
    .toList
  }

  override def getBody(httpResponse: HttpResponse, awaitFor: FiniteDuration): String = {
    Await.result(httpResponse.entity.toStrict(awaitFor)
                 .map(_.data.utf8String), awaitFor)
  }
}

case class AkkaHttpUtilsModule() extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[AkkaHttpClientUtils]).to(classOf[AkkaHttpClientUtilsImpl])
  }
}
