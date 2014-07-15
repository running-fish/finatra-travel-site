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
package finatra.travel.site.services

import com.twitter.util.Future

case class Advert(title: String, image: String)

class AdvertService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def adverts(count: Int, profile: Option[Profile]): Future[List[Advert]] = {
    client.get[List[Advert]](s"$baseUrl?count=$count${targetQueryString(profile)}", List.empty)
  }

  private def targetQueryString(profile: Option[Profile]) = {
    profile.map {
      p => s"&target=${target(p.spending)}"
    }.getOrElse("")
  }

  private def target(spending: String) = {
    spending match {
      case "Economy" => "low"
      case "Standard" => "middle"
      case "Luxury" => "high"
    }
  }
}

