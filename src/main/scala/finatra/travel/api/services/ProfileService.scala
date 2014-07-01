package finatra.travel.api.services

import com.twitter.util.Future
import com.twitter.finagle.HttpClient

object LifeCycle extends Enumeration {
  type LifeCycle = Value
  val Dependent, PreFamily, Family, Late = Value
}

object Spending extends Enumeration {
  type Spending = Value
  val Luxury, Standard, Economy = Value
}

object Gender extends Enumeration {
  type Gender = Value
  val Male, Female = Value
}

case class Profile(lifecycle: LifeCycle.LifeCycle, spending: Spending.Spending, gender: Gender.Gender)

class ProfileService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def profile(id: Option[String]): Future[Option[Profile]] = {
    id match {
      case Some(userId) => client.get(s"$baseUrl/user/$userId")
      case _ => Future.value(None)
    }
  }
}
