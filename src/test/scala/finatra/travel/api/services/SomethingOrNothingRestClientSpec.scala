package finatra.travel.api.services

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.util.{Duration, Await}
import com.github.tomakehurst.wiremock.client.WireMock._

case class Person(name: String, age: Int)

class SomethingOrNothingRestClientSpec extends FlatSpec with ShouldMatchers with WireMockSupport {

  val client = new SomethingOrNothingRestClient("localhost:9101")

  "Rest client get with default value" should "return the list of people from the remote service" in {
    stubGet("/people",
      "[ { \"name\":\"Fred\", \"age\":34 }, { \"name\":\"Barney\", \"age\":33 } ]"
    )

    val future = client.get[List[Person]]("/people", List.empty)
    val people = Await.result(future, Duration.fromSeconds(3))

    people should have size(2)

    people(0).name should be("Fred")
    people(1).name should be("Barney")
  }

  it should "return the default value if the remote service returns 500" in {
    stubFor(get(urlEqualTo("/people")).
      willReturn(aResponse().
        withStatus(500)))

    val future = client.get[List[Person]]("/people", List.empty)
    val people = Await.result(future, Duration.fromSeconds(3))

    people should have size(0)
  }

  it should "return the default value if the remote service returns 404" in {
    stubFor(get(urlEqualTo("/people")).
      willReturn(aResponse().
      withStatus(404)))

    val future = client.get[List[Person]]("/people", List.empty)
    val people = Await.result(future, Duration.fromSeconds(3))

    people should have size(0)
  }

  it should "return the default value if the remote service returns invalid json" in {
    stubGet("/people",
      "[ { \"firstName\":\"Fred\", \"age\":34 }, { \"firstName\":\"Barney\", \"age\":33 } ]"
    )

    val future = client.get[List[Person]]("/people", List.empty)
    val people = Await.result(future, Duration.fromSeconds(3))

    people should have size(0)
  }

  it should "return the default value if the remote service returns an empty body" in {
    stubGet("/people", "")

    val future = client.get[List[Person]]("/people", List.empty)
    val people = Await.result(future, Duration.fromSeconds(3))

    people should have size(0)
  }

  "Rest Client get with no default value" should "return an Option" in {
    stubGet("/person/123",
      "{ \"name\":\"Fred\", \"age\":34 }"
    )

    val future = client.get[Person]("/person/123")
    val people = Await.result(future, Duration.fromSeconds(3))

    people should be(Some(Person("Fred", 34)))
  }

  it should "return None if the remote server returns 500" in {
    stubFor(get(urlEqualTo("/person/123")).
      willReturn(aResponse().
      withStatus(500)))

    val future = client.get[Person]("/person/123")
    val people = Await.result(future, Duration.fromSeconds(3))

    people should be(None)
  }
}
