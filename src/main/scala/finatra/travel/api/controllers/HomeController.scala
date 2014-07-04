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

import finatra.travel.api.services._

class HomeController(secret: String, profileService: ProfileService, loyaltyService: LoyaltyService,
                     offersService: OffersService, userService: UserService)
  extends AuthController(secret, userService) {

  get("/") {
    OptionalAuth {
      request => {

        val futureProfile = profileService.profile(request.user)
        val futureLoyalty = loyaltyService.loyalty(request.user)

        val userData = for {
          profile <- futureProfile
          loyalty <- futureLoyalty
        } yield (profile, loyalty)

        userData flatMap {
          data => {
            offersService.offers(data._1, data._2) map {
              offers => {
                render.json(offers)
              }
            }
          }
        }
      }
    }
  }
}
