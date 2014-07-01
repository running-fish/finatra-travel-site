package finatra.travel.api.controllers

import com.twitter.finatra.Controller
import com.twitter.finagle.HttpClient
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpVersion, DefaultHttpRequest}
import org.jboss.netty.util.CharsetUtil
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import finatra.travel.api.services.{OffersService, LoyaltyService, ProfileService}
import com.twitter.util.Future

case class Offer(title: String, details: String, image: String)

class HomeController(profileService: ProfileService, loyaltyService: LoyaltyService, offersService: OffersService) extends Controller {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  get("/homeOld") {
    request => {
      val factory = HttpClient.newClient("localhost:9200")
      val client = factory.toService

      val futureOffers = client(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/offers"))

      futureOffers map {
        res => {
          println(res)
          val responseString = res.getContent().toString(CharsetUtil.UTF_8)
          val offers = mapper.readValue[List[Offer]](responseString)
          render.json(offers)
        }
      } rescue {
        case e => render.plain(e.toString).toFuture
      }
    }
  }

  get("/home") {
    request => {
      val userId = request.params.get("id")

      val userData = Future.join(
        profileService.profile(userId),
        loyaltyService.loyalty(userId)
      )

      userData flatMap {
        data => {
          offersService.offers(data._1, data._2) map {
            offers => {
              render.json(offers)
            }
          }
        }
      }
    }
  }
}
