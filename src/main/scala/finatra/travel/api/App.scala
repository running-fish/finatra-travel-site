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
package finatra.travel.api

import com.twitter.finatra._
import finatra.travel.api.controllers.{LoginController, HomeController}
import finatra.travel.api.services._

object App extends FinatraServer {

  // temporary hack
  System.setProperty("com.twitter.finatra.config.port", ":9000")

  private val applicationSecret = flag("applicationSecret",
    "woiegjv*j49ux^gew9)ijew,@-,mweHE9d(&dr3$", "The secret used for cookie signing")

  private val profileService = {
    val profileHost = flag("profile.host", "localhost:9100", "The host:port for the Profile Service")
    val profileUrl = flag("profile.url", "/profile", "The base url for the Profile Service")
    new ProfileService(profileHost(), profileUrl())
  }

  private val loyaltyService = {
    val loyaltyHost = flag("loyalty.host", "localhost:9100", "The host:port for the Loyalty Service")
    val loyaltyUrl = flag("loyalty.url", "/loyalty", "The base url for the Loyalty Service")
    new LoyaltyService(loyaltyHost(), loyaltyUrl())
  }

  private val offersService = {
    val offersHost = flag("offers.host", "localhost:9100", "The host:port for the Offers Service")
    val offersUrl = flag("offers.url", "/offers", "The base url for the Offers Service")
    new OffersService(offersHost(), offersUrl())
  }

  private val userService = {
    val userHost = flag("user.host", "localhost:9100", "The host:port for the User Service")
    val userUrl = flag("user.url", "/user", "The base url for the User Service")
    new UserService(userHost(), userUrl())
  }

  private val loginService = {
    val loginHost = flag("login.host", "localhost:9100", "The host:port for the Login Service")
    val loginUrl = flag("login.url", "/login", "The base url for the Login Service")
    new LoginService(loginHost(), loginUrl())
  }

  private val advertService = {
    val host = flag("advert.host", "localhost:9100", "The host:port for the Advert Service")
    val url = flag("advert.url", "/adverts", "The base url for the Advert Service")
    new AdvertService(host(), url())
  }

  private val weatherService = {
    val host = flag("weather.host", "api.openweathermap.org:80", "The host:port for the Weather Service")
    val url = flag("weather.url", "/data/2.5/forecast/daily", "The base url for the Weather Service")
    new WeatherService(host(), url())
  }

  register(new HomeController(applicationSecret(), profileService, loyaltyService, offersService, advertService, weatherService, userService))
  register(new LoginController(applicationSecret(), loginService))
}
