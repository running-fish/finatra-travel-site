package finatra.travel.api.controllers

import com.twitter.finatra.test.FlatSpecHelper
import org.scalatest.matchers.ShouldMatchers
import finatra.travel.api.services.{LoginService, WireMockSupport}
import com.twitter.finatra.FinatraServer
import org.jsoup.Jsoup.parse
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.WireMock
import com.jayway.jsonpath.JsonPath

class LoginControllerSpec extends FlatSpecHelper with ShouldMatchers with WireMockSupport {

  val loginService = new LoginService("localhost:9101", "/login")
  val loginController = new LoginController("sdhfdjfosdjsdf", loginService) with TestSession

  override val server = new FinatraServer
  server.register(loginController)

  "A get request to /login" should "return the login page" in {
    get("/login")

    response.code should be(200)

    val doc = parse(response.body)
    doc.select("title").text should be("Login")
  }

  "A browser post request to login" should "redirect to the home page if form fields are missing" in {
    post("/login", Map("username" -> "fred"), Map("Accept" -> "text/html"))

    response.code should be(302)
    response.getHeader("Location") should be("/")
  }

  it should "return 404 and the login form with an error message if the user is not found" in {
    stubFor(WireMock.post(urlEqualTo("/login")).
      withRequestBody(equalToJson("{ \"username\":\"fred\", \"password\":\"betty\" }"))
      willReturn(aResponse().
        withStatus(404)))

    post("/login", Map("username" -> "fred", "password" -> "betty"), Map("Accept" -> "text/html"))

    response.code should be(404)

    val doc = parse(response.body)
    doc.select(".formerror").text should be(loginController.invalidMessage)
  }

  it should "redirect to the home page with a session cookie if the user logs in successfully" in {
    stubPost("/login",
      "{ \"username\":\"fred\", \"password\":\"barney\" }",
      "{ \"id\":\"123\", \"name\":\"Fred\", \"username\":\"fredf\" }"
    )

    post("/login", Map("username" -> "fred", "password" -> "barney"), Map("Accept" -> "text/html"))

    response.code should be(302)
    response.getHeader("Location") should be("/")

    val sessionCookie = response.originalResponse.cookies.get(loginController.COOKIE_NAME)
    sessionCookie should not be(None)
    sessionCookie.get.value should be("123")
  }

  "A json post request to login" should "return 400 if form fields are missing" in {
    post("/login", Map("username" -> "fred"), Map("Accept" -> "application/json"))

    response.code should be(400)
    response.originalResponse.contentType should be(Some("application/json"))
  }

  it should "return 404 with an error message if the user is not found" in {
    stubFor(WireMock.post(urlEqualTo("/login")).
      withRequestBody(equalToJson("{ \"username\":\"fred\", \"password\":\"betty\" }"))
      willReturn(aResponse().
      withStatus(404)))

    post("/login",
      Map("username" -> "fred", "password" -> "betty"),
      Map("Accept" -> "application/json", "Content-Type" -> "application/x-www-form-urlencoded"))

    response.code should be(404)

    val errorMessage: String = JsonPath.read[String](response.body, "$.error")
    errorMessage should be(loginController.invalidMessage)
  }

  it should "return 200 with session cookie set if user logs in successfully" in {
    stubPost("/login",
      "{ \"username\":\"fred\", \"password\":\"barney\" }",
      "{ \"id\":\"456\", \"name\":\"Fred\", \"username\":\"fredf\" }"
    )

    post("/login",
      Map("username" -> "fred", "password" -> "barney"),
      Map("Accept" -> "application/json", "Content-Type" -> "application/x-www-form-urlencoded"))

    response.code should be(200)

    val sessionCookie = response.originalResponse.cookies.get(loginController.COOKIE_NAME)
    sessionCookie should not be(None)
    sessionCookie.get.value should be("456")
  }
}
