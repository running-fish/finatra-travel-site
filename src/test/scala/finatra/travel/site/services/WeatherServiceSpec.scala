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
package finatra.travel.site.services

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.io.InputStream
import scala.io.Source
import com.twitter.util.{Duration, Await}

class WeatherServiceSpec extends FlatSpec with ShouldMatchers with WireMockSupport {

  val weatherService = new WeatherService("localhost:9101", "/weather")

  private def stubWeather(url: String) {
    val inputStream: InputStream = getClass.getResourceAsStream("/weather-london-5days.json")
    val stubData = Source.fromInputStream(inputStream).mkString("")
    stubGet(url, stubData)
    inputStream.close()
  }

  "The weather service" should "return forecast for London" in {
    stubWeather("/weather?id=5290307&cnt=5&mode=json")
    val futureForecast = weatherService.forecast(5290307, 5)
    val forecast = Await.result(futureForecast, Duration.fromSeconds(3))
    forecast should not be(None)
  }

}
