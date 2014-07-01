package finatra.travel.api

import com.twitter.finatra._
import com.twitter.finatra.ContentType._
import finatra.travel.api.controllers.HomeController
import finatra.travel.api.services.{OffersService, LoyaltyService, ProfileService}

object App extends FinatraServer {

  val profileServiceHost = flag("profileServiceHost", "localhost:9200", "The host:port for the Profile Service")
  val profileServiceUrl = flag("profileServiceUrl", "/profile", "The base url for the Profile Service")
  val profileService = new ProfileService(profileServiceHost(), profileServiceUrl())

  val loyaltyServiceHost = flag("loyaltyServiceHost", "localhost:9200", "The host:port for the Loyalty Service")
  val loyaltyServiceUrl = flag("loyaltyServiceUrl", "/loyalty", "The base url for the Loyalty Service")
  val loyaltyService = new LoyaltyService(loyaltyServiceHost(), loyaltyServiceUrl())

  val offersServiceHost = flag("offersServiceHost", "localhost:9200", "The host:port for the Offers Service")
  val offersServiceUrl = flag("offersServiceUrl", "/offers", "The base url for the Offers Service")
  val offersService = new OffersService(offersServiceHost(), offersServiceUrl())

  register(new HomeController(profileService, loyaltyService, offersService)
}
