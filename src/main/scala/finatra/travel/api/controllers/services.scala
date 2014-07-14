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

import finatra.travel.api.App

trait ProfileService {
  val profileService = {
    val profileHost = App.flag("profile.host", "localhost:9100", "The host:port for the Profile Service")
    val profileUrl = App.flag("profile.url", "/profile", "The base url for the Profile Service")
    new finatra.travel.api.services.ProfileService(profileHost(), profileUrl())
  }
}

trait LoyaltyService {
  val loyaltyService = {
    val loyaltyHost = App.flag("loyalty.host", "localhost:9100", "The host:port for the Loyalty Service")
    val loyaltyUrl = App.flag("loyalty.url", "/loyalty", "The base url for the Loyalty Service")
    new finatra.travel.api.services.LoyaltyService(loyaltyHost(), loyaltyUrl())
  }
}

trait OffersService {
  val offersService = {
    val offersHost = App.flag("offers.host", "localhost:9100", "The host:port for the Offers Service")
    val offersUrl = App.flag("offers.url", "/offers", "The base url for the Offers Service")
    new finatra.travel.api.services.OffersService(offersHost(), offersUrl())
  }
}

trait UserService {
  val userService = {
    val userHost = App.flag("user.host", "localhost:9100", "The host:port for the User Service")
    val userUrl = App.flag("user.url", "/user", "The base url for the User Service")
    new finatra.travel.api.services.UserService(userHost(), userUrl())
  }
}

trait LoginService {
  val loginService = {
    val loginHost = App.flag("login.host", "localhost:9100", "The host:port for the Login Service")
    val loginUrl = App.flag("login.url", "/login", "The base url for the Login Service")
    new finatra.travel.api.services.LoginService(loginHost(), loginUrl())
  }
}

trait AdvertService {
  val advertService = {
    val host = App.flag("advert.host", "localhost:9100", "The host:port for the Advert Service")
    val url = App.flag("advert.url", "/adverts", "The base url for the Advert Service")
    new finatra.travel.api.services.AdvertService(host(), url())
  }
}

trait WeatherService {
  val weatherService = {
    val host = App.flag("weather.host", "api.openweathermap.org:80", "The host:port for the Weather Service")
    val url = App.flag("weather.url", "/data/2.5/forecast/daily", "The base url for the Weather Service")
    new finatra.travel.api.services.WeatherService(host(), url())
  }
}
