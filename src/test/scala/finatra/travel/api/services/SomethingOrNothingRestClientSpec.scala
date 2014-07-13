/**
 * Copyright 2014 Andy Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package finatra.travel.api.services

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.util.{Duration, Await}
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import scala.collection.JavaConverters._

case class Person(name: String, age: Int) {
  require(name != null, "Name is required")
}

case class PostResult(status: Int, message: String)

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

  it should "ignore unknown properties in json" in {
    stubGet("/people",
      "[ { \"name\":\"Fred\", \"age\":34, \"lastName\":\"Flintstone\" }, " +
        "{ \"name\":\"Barney\", \"age\":33, \"lastName\":\"Rubble\" } ]"
    )

    val future = client.get[List[Person]]("/people", List.empty)
    val people = Await.result(future, Duration.fromSeconds(3))

    people should have size(2)
    people(0).name should be("Fred")
    people(0).age should be(34)
  }

  it should "return the defalt value if the remote service returns json with missing properties that are required" in {
    stubGet("/people",
      "[ { \"firstName\":\"Fred\", \"age\":34, \"lastName\":\"Flintstone\" }, " +
        "{ \"firstName\":\"Barney\", \"age\":33, \"lastName\":\"Rubble\" } ]"
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

  "Rest Client post" should "post the body as json and return Some(PostResult)" in {
    stubFor(post(urlEqualTo("/login")).
      withHeader("Content-Type", equalTo("application/json")).
      withRequestBody(equalToJson("{ \"name\":\"bill\", \"age\":23 }")).
      willReturn(aResponse().
        withStatus(200).
        withHeader("Content-Type", "application/json").
        withBody("{ \"status\":0, \"message\":\"success\"}")))

    val future = client.post[Person, PostResult]("/login", Person("bill", 23))
    val result = Await.result(future, Duration.fromSeconds(3))

    result should be(Some(PostResult(0, "success")))
  }
}
