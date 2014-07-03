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

import com.twitter.finatra.Controller
import finatra.travel.api.views.LoginView
import finatra.travel.api.services.LoginService

class LoginController(secret: String, loginService: LoginService) extends Controller with Session {

  get("/login") {
    request => {
      val view = new LoginView
      render.view(view).toFuture
    }
  }

  post("/login") {
    request => {
      (request.params.get("username"), request.params.get("password")) match {
        case (Some(username), Some(password)) => loginService.login(username, password) map {
          user => {
            user match {
              case Some(u) => render.json(Map.empty).cookie(toCookie(secret, u.id))
              case _ => {
                val view = new LoginView(Some("Invalid username or password"))
                render.view(view)
              }
            }
          }
        }
        case _ => redirect("/").toFuture
      }
    }
  }
}
