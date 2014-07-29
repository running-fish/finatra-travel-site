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
package finatra.travel.site.controllers

import org.scalatest.matchers.ShouldMatchers
import finatra.travel.site.services._
import com.twitter.finatra.test.FlatSpecHelper
import com.twitter.finatra.FinatraServer
import org.jsoup.Jsoup.parse

class HomeControllerSpec extends FlatSpecHelper with ShouldMatchers with WireMockSupport {

  val homeController = new HomeController("oweigowghoweihgowhgowehg") with
    TestProfileService with TestLoyaltyService with TestOffersService with
    TestAdvertService with TestWeatherService with TestUserService

  override val server = new FinatraServer
  server.register(homeController)

  "A get request to /" should "return the home page with a login link if no-one is logged in" in {
    stubGet("/offers",
      "[ { \"title\":\"Offer Foo\", \"details\":\"Details Foo\", \"image\":\"foo.jpg\" } ]"
    )
    stubGet("/adverts?count=5",
      "[{ \"title\":\"Advert 1\", \"image\":\"advert1.jpg\" }, " +
        "{ \"title\":\"Advert 1\", \"image\":\"advert1.jpg\" }, " +
        "{ \"title\":\"Advert 1\", \"image\":\"advert1.jpg\" }, " +
        "{ \"title\":\"Advert 1\", \"image\":\"advert1.jpg\" }, " +
        "{ \"title\":\"Advert 1\", \"image\":\"advert1.jpg\" }]"
    )

    get("/", Map.empty, Map("Accept" -> "text/html"))
    response.code should equal(200)

    val doc = parse(response.body)
    doc.select("span.offer") should have size 1
    doc.select("span.advert") should have size 4
    doc.select("span#loginlink") should have size 1
    doc.select("span#loginlink").hasClass("hidden") should be(false)
    doc.select("span#loginlink").hasClass("visible") should be(true)
    doc.select("span#logoutlink") should have size 1
    doc.select("span#logoutlink").hasClass("hidden") should be(true)
    doc.select("span#logoutlink").hasClass("visible") should be(false)
  }
}
