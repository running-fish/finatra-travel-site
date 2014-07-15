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

case class Location(cityId: Int, latitude: Double, longitude: Double)

case class Address(firstLine: String, town: String, postcode: String, countryCode: String, location: Location)

case class User(id: String, name: String, username: String, address: Option[Address])

class UserService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def user(id: Option[String]): Future[Option[User]] = {
    id match {
      case Some(i) => client.get[User](s"$baseUrl?id=$i")
      case _ => Future.value(None)
    }
  }
}
