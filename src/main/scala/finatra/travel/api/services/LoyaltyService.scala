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

object Group extends Enumeration {
  type Group = Value
  val Bronze, Silver, Gold = Value
}

case class Loyalty(group: Group.Group, points: Int)

class LoyaltyService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def loyalty(id: Option[String]): Future[Option[Loyalty]] = {
    id match {
      case Some(userId) => client.get(s"$baseUrl/user/$userId")
      case _ => Future.value(None)
    }
  }
}