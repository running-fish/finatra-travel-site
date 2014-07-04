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

import org.scalatest.{Suite, BeforeAndAfterEach}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.WireMock

trait WireMockSupport extends BeforeAndAfterEach {
  self: Suite =>

  val wireMockServer = new WireMockServer(wireMockConfig().port(9101))

  override def beforeEach() {
    wireMockServer.start()
    configureFor("localhost", 9101)
    WireMock.reset()
  }

  override def afterEach() {
    wireMockServer.stop()
  }

  def stubGet(url: String, body: String) {
    stubFor(get(urlEqualTo(url)).
      willReturn(aResponse().
        withHeader("Content-Type", "application/json").
        withBody(body)))
  }

  def stubPost(url: String, body: String, response: String) {
    stubFor(post(urlEqualTo(url)).
      withRequestBody(equalToJson(body))
      willReturn(aResponse().
        withHeader("Content-Type", "application/json").
        withBody(response)))
  }
}
