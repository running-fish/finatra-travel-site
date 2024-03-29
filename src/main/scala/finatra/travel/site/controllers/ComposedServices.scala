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

import finatra.travel.site.services._
import com.twitter.util.Future
import finatra.travel.site.services.Loyalty
import finatra.travel.site.services.Advert
import finatra.travel.site.services.Profile
import finatra.travel.site.services.User

trait ComposedServices {
  self: ProfileService with LoyaltyService with AdvertService with OffersService =>

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
