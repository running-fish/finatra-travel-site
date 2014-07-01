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
import com.twitter.finatra.ContentType._
import finatra.travel.api.controllers.HomeController
import finatra.travel.api.services.{OffersService, LoyaltyService, ProfileService}

object App extends FinatraServer {

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

  register(new HomeController(profileService, loyaltyService, offersService))
}
