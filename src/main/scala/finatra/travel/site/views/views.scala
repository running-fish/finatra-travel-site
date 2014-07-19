package finatra.travel.site.views

import finatra.travel.site.services._
import com.twitter.finatra.View
import finatra.travel.site.services.Offer
import finatra.travel.site.services.Advert
import finatra.travel.site.services.User
import scala.Some
import finatra.travel.site.services.DailyForecast

case class OffersView(offers: List[Offer])

case class AdvertsView(adverts: List[Advert])

case class HomePageView(user: Option[User],
                        offers: Option[OffersView],
                        adverts: Option[AdvertsView],
                        mainAdvert: Option[Advert],
                        weather: Option[DailyForecast],
                        assets: String = "") extends View {
  def template: String = "home.mustache"
}

object HomePageView {
  def from(user: Option[User],
           offers: List[Offer],
           adverts: List[Advert],
           weather: Option[DailyForecast]): HomePageView = {
    HomePageView(
      user,
      if (offers.isEmpty) None else Some(OffersView(offers)),
      if (adverts.isEmpty) None else Some(AdvertsView(adverts.take(4))),
      if (adverts.size > 4) Some(adverts(4)) else None,
      weather
    )
  }
}

case class OffersPageView(user: Option[User],
                          offers: Option[OffersView],
                          adverts: Option[AdvertsView],
                          assets: String = "") extends View {
  def template: String = "offers.mustache"
}

object OffersPageView {
  def from(user: Option[User],
           offers: List[Offer],
           adverts: List[Advert]): OffersPageView = {
    OffersPageView(
      user,
      if (offers.isEmpty) None else Some(OffersView(offers)),
      if (adverts.isEmpty) None else Some(AdvertsView(adverts))
    )
  }
}

case class NewsView(news: List[NewsItem])

case class NewsPageView(user: Option[User],
                        news: Option[NewsView]) extends View {
  def template: String = "news.mustache"
}

object NewsPageView {
  def from(user: Option[User], news: List[NewsItem]): NewsPageView = {
    NewsPageView(
      user,
      if (news.isEmpty) None else Some(NewsView(news))
    )
  }
}
