package finatra.travel.api.views

import finatra.travel.api.services.{Advert, Offer, User}
import com.twitter.finatra.View

case class OffersView(offers: List[Offer])

case class AdvertsView(adverts: List[Advert])

case class HomeView(user: Option[User], offers: Option[OffersView], adverts: Option[AdvertsView], assets: String = "") extends View {
  def template: String = "home.mustache"
}

object HomeView {
  def from(user: Option[User], offers: List[Offer], adverts: List[Advert]): HomeView = {
    HomeView(
      user,
      if (offers.isEmpty) None else Some(OffersView(offers)),
      if (adverts.isEmpty) None else Some(AdvertsView(adverts))
    )
  }
}

class LoginView(val error: Option[String] = None) extends View {
  def template: String = "login.mustache"
}
