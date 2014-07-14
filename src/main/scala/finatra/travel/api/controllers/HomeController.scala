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
import com.twitter.finatra.ContentType.{Html, Json}
import finatra.travel.api.views.HomeView
import com.twitter.logging.Logger
import com.twitter.util.Future

class HomeController(secret: String, profileService: ProfileService, loyaltyService: LoyaltyService,
                     offersService: OffersService, advertService: AdvertService, weatherService: WeatherService,
                     userService: UserService)
  extends AuthController(secret, userService) {

  get("/") {
    OptionalAuth {
      request => {

        profileLoyalty(request.user) flatMap {
          advertsOffers(5)
        } flatMap {
          adsAndOffers => {
            val adverts = adsAndOffers._1
            val offers = adsAndOffers._2
            val view = HomeView.from(request.user, offers.take(4), adverts)
            log.info(view.toString)
            respondTo(request) {
              case _:Json => render.json(view).toFuture
              case _:Html => render.view(view).toFuture
            }
          }
        }
      }
    }
  }

  def profileLoyalty(user: Option[User]): Future[(Option[Profile], Option[Loyalty])] = {
    val futureProfile = profileService.profile(user)
    val futureLoyalty = loyaltyService.loyalty(user)
    for {
      profile <- futureProfile
      loyalty <- futureLoyalty
    } yield (profile, loyalty)
  }

  def advertsOffers(advertsCount: Int)(profileLoyalty: (Option[Profile], Option[Loyalty])): Future[(List[Advert], List[Offer])] = {
    val futureAdverts = advertService.adverts(advertsCount, profileLoyalty._1)
    val futureOffers = offersService.offers(profileLoyalty._1, profileLoyalty._2)
    for {
      adverts <- futureAdverts
      offers <- futureOffers
    } yield(adverts, offers)
  }
}
