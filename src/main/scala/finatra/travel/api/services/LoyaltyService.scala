package finatra.travel.api.services

import com.twitter.util.Future

object Group extends Enumeration {
  type Group = Value
  val Bronze, Silver, Gold = Value
}

case class Loyalty(group: Group.Group, points: Int)

class LoyaltyService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def loyalty(id: Option[String]): Future[Option[Loyalty]] = {
    id match {
      case Some(userId) => client.get(s"$baseUrl/user/$userId")
      case _ => Future.value(None)
    }
  }
}
