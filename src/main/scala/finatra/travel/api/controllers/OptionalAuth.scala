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

import com.twitter.finagle.http.{Request => FinagleRequest}
import com.twitter.finatra.Request
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.Future

case class User(id: String)

class OptionalUserRequest(request: FinagleRequest, val user: Option[User]) extends Request(request) {
}

object OptionalAuth {
  def apply(action: OptionalUserRequest => Future[ResponseBuilder]): Request => Future[ResponseBuilder] = {
    request => {
      // TEMP: get user id from request query string ...
      // TODO: replace this with "session" cookie and UserService, like the play version
      val user = request.params.get("id") map {
        id => User(id)
      }
      action(new OptionalUserRequest(request, user))
    }
  }
}
