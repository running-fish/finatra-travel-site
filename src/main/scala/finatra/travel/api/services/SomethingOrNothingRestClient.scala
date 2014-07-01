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

import com.twitter.finagle.{HttpClient, Service}
import org.jboss.netty.handler.codec.http._
import com.twitter.util.Future
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.Some
import org.jboss.netty.util.CharsetUtil

class SomethingOrNothingRestClient(host: String) {

  private val service = HttpClient.newClient(host).toService

  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def get[R](url: String)(implicit manifest: Manifest[R]): Future[Option[R]] = {
    getInternal[R, Option[R]](url, None) { result => Some(result) }
  }

  def get[R](url: String, defaultValue: R)(implicit manifest: Manifest[R]): Future[R] = {
    getInternal[R, R](url, defaultValue) { result => result }
  }

  private def getInternal[R, S](url: String, defaultValue: S)(f: R => S)(implicit manifest: Manifest[R]): Future[S] = {
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url)
    request.headers().add("Accept", "application/json")
    request.headers().add("Host", host)

    service(request) map {
      response => {
        val body = response.getContent.toString(CharsetUtil.UTF_8)
        val result = mapper.readValue[R](body)
        f(result)
      }
    } rescue {
      case e => {
        Future.value(defaultValue)
      }
    }
  }
}
