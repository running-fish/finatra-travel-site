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

object TestServiceSettings {
  val host = "localhost:9101"
}

trait TestProfileService extends ProfileService {
  override val profileService = new finatra.travel.site.services.ProfileService(TestServiceSettings.host, "/profile")
}

trait TestLoyaltyService extends LoyaltyService {
  override val loyaltyService = new finatra.travel.site.services.LoyaltyService(TestServiceSettings.host, "/loyalty")
}

trait TestOffersService extends OffersService {
  override val offersService = new finatra.travel.site.services.OffersService(TestServiceSettings.host, "/offers")
}

trait TestUserService extends UserService {
  override val userService = new finatra.travel.site.services.UserService(TestServiceSettings.host, "/user")
}

trait TestLoginService extends LoginService {
  override val loginService = new finatra.travel.site.services.LoginService(TestServiceSettings.host, "/login")
}

trait TestAdvertService extends AdvertService {
  override val advertService = new finatra.travel.site.services.AdvertService(TestServiceSettings.host, "/adverts")
}

trait TestWeatherService extends WeatherService {
  override val weatherService = new finatra.travel.site.services.WeatherService(TestServiceSettings.host, "/weather")
}
