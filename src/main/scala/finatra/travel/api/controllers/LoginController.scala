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

import com.twitter.finatra.{Request, ResponseBuilder, Controller}
import finatra.travel.api.services.{User, LoginService}
import com.twitter.util.Future

class LoginController(secret: String, loginService: LoginService) extends Controller with Session {

  val invalidMessage = "Invalid username or password"
  val errorMessage = "Sorry, but there was a problem checking your credentials, please try again in a few minutes"

  post("/login") {
    implicit request => {
      (request.params.get("username"), request.params.get("password")) match {
        case (Some(username), Some(password)) => login(username, password)
        case _ => invalidLoginSubmission
      }
    }
  }

  private def login(username: String, password: String)(implicit request: Request): Future[ResponseBuilder] = {
    loginService.login(username, password) flatMap {
      user => verifyUser(user)
    } rescue {
      // this will never be executed ... the login service returns None if an exception is thrown calling the
      // remote server ... might want to change that to provide a more informative/useful error?
      case e => loginException(e)
    }
  }

  private def verifyUser(user: Option[User])(implicit request: Request): Future[ResponseBuilder] = {
    user match {
      case Some(u) => userFound(u)
      case _ => userNotFound
    }
  }

  private def userFound(user: User)(implicit request: Request): Future[ResponseBuilder] = {
    render.json(Map.empty).cookie(toCookie(secret, user.id)).toFuture
  }

  private def userNotFound()(implicit request: Request): Future[ResponseBuilder] = {
    render.status(404).json(Map("error" -> invalidMessage)).toFuture
  }

  private def invalidLoginSubmission()(implicit request: Request): Future[ResponseBuilder] = {
    render.json(Map.empty).status(400).toFuture
  }

  private def loginException(e: Throwable)(implicit request: Request) = {
    render.status(500).json(Map("error" -> errorMessage)).toFuture
  }
}
