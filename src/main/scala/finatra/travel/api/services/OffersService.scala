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
package finatra.travel.api.services

import com.twitter.util.Future

case class Offer(title: String, details: String, image: String)

class OffersService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def offers(profile: Option[Profile], loyalty: Option[Loyalty]): Future[List[Offer]] = {
    val queryString = List(profileQueryString(profile), loyaltyQueryString(loyalty)).flatten.mkString("?", "&", "")
    client.get[List[Offer]](baseUrl + queryString, List.empty)
  }

  def profileQueryString(profile: Option[Profile]) = {
    profile map { p =>
      s"lifecycle=${p.lifecycle.toString.toLowerCase}&spending=${p.spending.toString.toLowerCase}&gender=${p.gender.toString.toLowerCase}"
    }
  }

  def loyaltyQueryString(loyalty: Option[Loyalty]) = {
    loyalty map { l =>
      s"loyalty=${l.group.toString.toLowerCase}&points=${l.points.toString}"
    }
  }

}

