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

import com.twitter.finatra.{Request, ResponseBuilder, Controller}
import finatra.travel.site.services.{LoginData, User}
import com.twitter.util.{Duration, Future}
import com.fasterxml.jackson.databind.{JsonMappingException, DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.finagle.http.Cookie
import java.util.concurrent.TimeUnit

class LoginController(secret: String) extends Controller with Session with LoginService {

  val invalidMessage = "Invalid username or password"
  val errorMessage = "Sorry, but there was a problem checking your credentials, please try again in a few minutes"

  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  mapper.registerModule(DefaultScalaModule)

  post("/login") {
    implicit request => {
      try {
        val loginData = mapper.readValue[LoginData](request.getContentString())
        login(loginData.username, loginData.password)
      } catch {
        case jme: JsonMappingException => invalidLoginSubmission
      }
    }
  }

  get("/logout") {
    request => {
      val cookie = new Cookie("GETAWAY_SESSION", "")
      cookie.maxAge = Duration(0, TimeUnit.SECONDS)
      render.status(303).header("Location", "/").cookie(cookie).toFuture
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

  private def invalidLoginSubmission()(implicit request: Request): Future[ResponseBuilder] = {
    render.json(Map.empty).status(400).toFuture
  }

  private def userFound(user: User)(implicit request: Request): Future[ResponseBuilder] = {
    render.json(Map.empty).cookie(toCookie(secret, user.id)).toFuture
  }

  private def userNotFound()(implicit request: Request): Future[ResponseBuilder] = {
    render.status(404).json(Map("error" -> invalidMessage)).toFuture
  }

  private def loginException(e: Throwable)(implicit request: Request) = {
    render.status(500).json(Map("error" -> errorMessage)).toFuture
  }
}
