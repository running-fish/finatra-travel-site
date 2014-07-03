package finatra.travel.api.services

import com.twitter.finagle.HttpClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.util.Future

case class LoginData(username: String, password: String)

class LoginService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def login(username: String, password: String): Future[Option[User]] = {
    client.post[LoginData, User](baseUrl, LoginData(username, password))
  }
}
