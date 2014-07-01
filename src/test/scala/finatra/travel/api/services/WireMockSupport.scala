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
}
