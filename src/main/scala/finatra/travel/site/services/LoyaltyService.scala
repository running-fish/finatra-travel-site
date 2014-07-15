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

case class Loyalty(group: String, points: Int)

class LoyaltyService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def loyalty(user: Option[User]): Future[Option[Loyalty]] = {
    user match {
      case Some(u) => client.get[Loyalty](s"$baseUrl/user/${u.id}")
      case _ => Future.value(None)
    }
  }
}
