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

import finatra.travel.api.services.Location

object GeoLocator {

  private val centralLondon = Location(2643741, 51.512791, -0.091840)

  def locate(request: OptionalUserRequest): Location = {
    val userLocation = request.user flatMap {
      u => u.address map {
        a => a.location
      }
    }
    // TODO: if no user data, try ip address

    // return default for now
    userLocation.getOrElse(centralLondon)
  }
}

