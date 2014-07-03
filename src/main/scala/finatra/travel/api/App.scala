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
  System.setProperty("com.twitter.finatra.config.port", ":9100")

  val profileServiceHost = flag("profileServiceHost", "localhost:9200", "The host:port for the Profile Service")
  val profileServiceUrl = flag("profileServiceUrl", "/profile", "The base url for the Profile Service")
  val profileService = new ProfileService(profileServiceHost(), profileServiceUrl())

  val loyaltyServiceHost = flag("loyaltyServiceHost", "localhost:9200", "The host:port for the Loyalty Service")
  val loyaltyServiceUrl = flag("loyaltyServiceUrl", "/loyalty", "The base url for the Loyalty Service")
  val loyaltyService = new LoyaltyService(loyaltyServiceHost(), loyaltyServiceUrl())

  val offersServiceHost = flag("offersServiceHost", "localhost:9200", "The host:port for the Offers Service")
  val offersServiceUrl = flag("offersServiceUrl", "/offers", "The base url for the Offers Service")
  val offersService = new OffersService(offersServiceHost(), offersServiceUrl())

  val userServiceHost = flag("userServiceHost", "localhost:9200", "The host:port for the User Service")
  val userServiceUrl = flag("userServiceUrl", "/user", "The base url for the User Service")
  val userService = new UserService(userServiceHost(), userServiceUrl())

  val loginServiceHost = flag("loginServiceHost", "localhost:9200", "The host:port for the Login Service")
  val loginServiceUrl = flag("loginServiceUrl", "/login", "The base url for the Login Service")
  val loginService = new LoginService(loginServiceHost(), loginServiceUrl())

  val applicationSecret = flag("applicationSecret", "woiegjv*j49ux^gew9)ijew,@-,mweHE9d(&dr3$", "The secret used for cookie signing")

  premain {
    registerControllers()
  }

  def registerControllers() {
    register(new HomeController(applicationSecret(), profileService, loyaltyService, offersService, userService))
    register(new LoginController(applicationSecret(), loginService))
  }
}
