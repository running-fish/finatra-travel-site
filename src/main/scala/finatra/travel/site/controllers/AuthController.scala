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

import com.twitter.finagle.http.{Request => FinagleRequest}
import finatra.travel.site.services.User
import com.twitter.finatra.{Request, ResponseBuilder, Controller}
import com.twitter.util.Future

class OptionalUserRequest(request: FinagleRequest, val user: Option[User]) extends Request(request)

class AuthController(secret: String) extends Controller with Session with UserService {

  object OptionalAuth {
    def apply(action: OptionalUserRequest => Future[ResponseBuilder]): Request => Future[ResponseBuilder] = {
      request => {
        userService.user(fromCookies(secret, request.cookies)) flatMap {
          user => action(new OptionalUserRequest(request, user))
        }
      }
    }
  }
}
