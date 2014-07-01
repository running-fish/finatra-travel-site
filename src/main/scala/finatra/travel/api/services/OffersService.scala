package finatra.travel.api.services

import com.twitter.util.Future

case class Offer(title: String, details: String, image: String)

class OffersService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def offers(profile: Option[Profile], loyalty: Option[Loyalty]): Future[List[Offer]] = {
    val queryString = List(profileQueryString(profile), loyaltyQueryString(loyalty)).flatten.mkString("?", "&", "")
    client.get[List[Offer]](baseUrl + queryString, List.empty)
  }

  def profileQueryString(profile: Option[Profile]) = {
    profile map { p =>
      s"lifecycle=${p.lifecycle.toString.toLowerCase}&spending=${p.spending.toString.toLowerCase}&gender=${p.gender.toString.toLowerCase}"
    }
  }

  def loyaltyQueryString(loyalty: Option[Loyalty]) = {
    loyalty map { l =>
      s"loyalty=${l.group.toString.toLowerCase}&points=${l.points.toString}"
    }
  }

}

