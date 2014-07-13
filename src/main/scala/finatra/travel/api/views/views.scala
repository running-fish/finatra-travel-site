package finatra.travel.api.views

import finatra.travel.api.services.{Offer, User}
import com.twitter.finatra.View
import finatra.travel.api.services.Offer

case class OffersView(offers: List[Offer])

case class HomeView(user: Option[User], offers: Option[OffersView], assets: String = "") extends View {
  def template: String = "home.mustache"
}

object HomeView {
  def from(user: Option[User], offers: List[Offer]): HomeView = {
    HomeView(
      user,
      if (offers.isEmpty) None else Some(OffersView(offers))
    )
  }
}

class LoginView(val error: Option[String] = None) extends View {
  def template: String = "login.mustache"
}
