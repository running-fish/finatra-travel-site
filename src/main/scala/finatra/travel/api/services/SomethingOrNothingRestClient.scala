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

import com.twitter.finagle.HttpClient
import org.jboss.netty.handler.codec.http._
import com.twitter.util.Future
import com.fasterxml.jackson.databind.{JavaType, DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.Some
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.buffer.ChannelBuffers
import com.twitter.logging.Logger
import com.twitter.finatra.config

class SomethingOrNothingRestClient(host: String) {

  private val service = HttpClient.newClient(host).toService

  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  mapper.registerModule(DefaultScalaModule)

  def log = Logger(config.logNode())

  def get[R](url: String)(implicit manifest: Manifest[R]): Future[Option[R]] = {
    getInternal[R, Option[R]](url, None) { result => Some(result) }
  }

  def get[R](url: String, defaultValue: R)(implicit manifest: Manifest[R]): Future[R] = {
    getInternal[R, R](url, defaultValue) { result => result }
  }

  def post[B, R](url: String, body: B)(implicit manifestB: Manifest[B], manifestR: Manifest[R]): Future[Option[R]] = {
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url)
    request.headers().add("Content-Type", "application/json")
    request.headers().add("Host", host)

    val content = mapper.writeValueAsString(body)
    request.setContent(ChannelBuffers.copiedBuffer(content, CharsetUtil.UTF_8))
    request.headers().add("Content-Length", content.length)

    execute[R, Option[R]](request, None) { result => Some(result) }
  }

  private def getInternal[R, S](url: String, defaultValue: S)(f: R => S)(implicit manifest: Manifest[R]): Future[S] = {
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url)
    request.headers().add("Accept", "application/json")
    request.headers().add("Host", host)
    execute[R, S](request, defaultValue)(f)
  }

  private def execute[R, S](request: HttpRequest, defaultValue: S)(f: R => S)(implicit manifest: Manifest[R]): Future[S] = {
    log.info(s"SomethingOrNothingRestClient executing $request")
    service(request) map {
      response => {
        log.info(s"SomethingOrNothingRestClient response status ${response.getStatus}")
        response.getStatus.getCode match {
          case 200 => {
            val body = response.getContent.toString(CharsetUtil.UTF_8)
            log.info(s"SomethingOrNothingRestClient response content $body")
            val result = mapper.readValue[R](body)
            f(result)
          }
          case _ => defaultValue
        }

      }
    } rescue {
      case e => {
        Future.value(defaultValue)
      }
    }
  }
}
