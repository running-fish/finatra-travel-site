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
package finatra.travel.api.controllers

import org.scalatest.matchers.ShouldMatchers
import finatra.travel.api.services._
import com.twitter.finatra.test.FlatSpecHelper
import com.twitter.finatra.FinatraServer

class HomeControllerSpec extends FlatSpecHelper with ShouldMatchers with WireMockSupport {

  val host = "localhost:9101"
  val profileService = new ProfileService(host, "/profile")
  val loyaltyService = new LoyaltyService(host, "/loyalty")
  val offersService = new OffersService(host, "/offers")
  val userService = new UserService(host, "/user")
  val advertService = new AdvertService(host, "adverts")
  val weatherService = new WeatherService(host, "adverts")

  override val server = new FinatraServer
  server.register(new HomeController("oweigowghoweihgowhgowehg", profileService, loyaltyService, offersService,
    advertService, weatherService, userService))

  "Home Controller" should "return a list of offers" in {
    stubGet("/offers",
      "[ { \"title\":\"Offer Foo\", \"details\":\"Details Foo\", \"image\":\"foo.jpg\" } ]"
    )

    get("/", Map.empty, Map("Accept" -> "application/json"))
    response.code should equal(200)
    println("RESPONSE:" + response.body)
  }
}
