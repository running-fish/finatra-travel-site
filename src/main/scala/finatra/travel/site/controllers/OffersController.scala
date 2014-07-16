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
import finatra.travel.site.services.Advert
import finatra.travel.site.services.User
import finatra.travel.site.views.OffersPageView
import com.twitter.finatra.ContentType.{Html, Json}

class OffersController(secret: String)
  extends AuthController(secret)
  with ProfileService with LoyaltyService with OffersService with AdvertService with ComposedServices {

  get("/offers") {
    OptionalAuth {
      request => {

        advertsOffers(request.user, 4) flatMap {
          result => {
            val adverts = result._1
            val offers = result._2
            val view = OffersPageView.from(request.user, offers, adverts)
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

  def advertsOffers(user: Option[User], numberOfAdverts: Int): Future[(List[Advert], List[Offer])] = {
    profileLoyalty(user) flatMap {
      advertsOffers(numberOfAdverts)
    }
  }
}

