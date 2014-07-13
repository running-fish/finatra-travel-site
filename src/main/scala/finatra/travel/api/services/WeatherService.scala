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

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.math.BigDecimal.RoundingMode
import com.twitter.util.Future

case class City(name: String)

case class Weather(main: String, description: String, icon: String)

case class Temperatures(min: BigDecimal, max: BigDecimal)

case class Forecast(dt: Int, temperatures: Temperatures, weather: Seq[Weather], speed: BigDecimal, deg: BigDecimal) {

  private val secondsInDay = 60 * 60 * 24

  private val directions = Array(
    "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"
  )

  val date = LocalDate.ofEpochDay(dt * secondsInDay)

  val day = date.format(DateTimeFormatter.ofPattern("EEEE"))

  val minimum = temperatures.min

  val maximum = temperatures.max

  val summary = s"${weather(0).main}, ${weather(0).description}"

  val icon = weather(0).icon

  val windSpeed = speed

  val windDirection = directions((deg / 22.5).setScale(0, RoundingMode.HALF_DOWN).toInt)
}

case class DailyForecast(city: String, list: Seq[Forecast])

class WeatherService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def forecast(cityId: Int, numberOfDays: Int): Future[Option[DailyForecast]] = {
    val queryString = s"?id=$cityId&cnt=$numberOfDays&mode=json"
    client.get(s"baseUrl$queryString")
  }
}
