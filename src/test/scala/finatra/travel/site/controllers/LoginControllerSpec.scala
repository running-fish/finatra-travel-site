package finatra.travel.site.controllers

import com.twitter.finatra.test.FlatSpecHelper
import org.scalatest.matchers.ShouldMatchers
import finatra.travel.site.services.WireMockSupport
import com.twitter.finatra.FinatraServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.WireMock
import com.jayway.jsonpath.JsonPath

class LoginControllerSpec extends FlatSpecHelper with ShouldMatchers with WireMockSupport {

  val loginController = new LoginController("sdhfdjfosdjsdf") with TestSession with TestLoginService

  override val server = new FinatraServer
  server.register(loginController)

  "A json post request to login" should "return 400 if form fields are missing" in {
    post(path = "/login",
      headers = Map("Accept" -> "application/json", "Content-Type" -> "application/json"),
      body = "{ \"username\":\"fred\"}"
    )

    response.code should be(400)
    response.originalResponse.contentType should be(Some("application/json"))
  }

  it should "return 404 with an error message if the user is not found" in {
    stubFor(WireMock.post(urlEqualTo("/login")).
      withRequestBody(equalToJson("{ \"username\":\"fred\", \"password\":\"barney\" }"))
      willReturn(aResponse().
      withStatus(404)))

    post(path = "/login",
      headers = Map("Accept" -> "application/json", "Content-Type" -> "application/x-www-form-urlencoded"),
      body = "{ \"username\":\"fred\", \"password\":\"barney\"}"
    )

    response.code should be(404)

    val errorMessage: String = JsonPath.read[String](response.body, "$.error")
    errorMessage should be(loginController.invalidMessage)
  }

  it should "return 200 with session cookie set if user logs in successfully" in {
    stubPost("/login",
      "{ \"username\":\"fred\", \"password\":\"barney\" }",
      "{ \"id\":\"456\", \"name\":\"Fred\", \"username\":\"fredf\" }"
    )

    post(path = "/login",
      headers = Map("Accept" -> "application/json", "Content-Type" -> "application/x-www-form-urlencoded"),
      body = "{ \"username\":\"fred\", \"password\":\"barney\"}"
    )

    response.code should be(200)

    val sessionCookie = response.originalResponse.cookies.get(loginController.COOKIE_NAME)
    sessionCookie should not be(None)
    sessionCookie.get.value should be("456")
  }
}
