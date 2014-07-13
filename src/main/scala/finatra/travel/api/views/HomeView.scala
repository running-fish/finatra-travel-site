package finatra.travel.api.views

import com.twitter.finatra.View
import finatra.travel.api.services.{Offer, User}

class HomeView(val user: Option[User], val offers: List[Offer]) extends View {

  def template: String = "mustache/home.mustache"
}
