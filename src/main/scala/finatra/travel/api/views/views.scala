package finatra.travel.api.views

import finatra.travel.api.services.{DailyForecast, Advert, Offer, User}
import com.twitter.finatra.View

case class OffersView(offers: List[Offer])

case class AdvertsView(adverts: List[Advert])

case class HomeView(user: Option[User], offers: Option[OffersView], adverts: Option[AdvertsView],
                    mainAdvert: Option[Advert], weather: Option[DailyForecast], assets: String = "") extends View {
  def template: String = "home.mustache"
}

object HomeView {
  def from(user: Option[User], offers: List[Offer], adverts: List[Advert], weather: Option[DailyForecast]): HomeView = {
    HomeView(
      user,
      if (offers.isEmpty) None else Some(OffersView(offers)),
      if (adverts.isEmpty) None else Some(AdvertsView(adverts.take(4))),
      if (adverts.size > 4) Some(adverts(4)) else None,
      weather
    )
  }
}
