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

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.math.BigDecimal.RoundingMode
import com.twitter.util.Future

case class City(name: String) {
  override def toString = name
}

case class Weather(main: String, description: String, icon: String)

case class Temperatures(min: java.math.BigDecimal, max: java.math.BigDecimal)

case class Forecast(dt: Int, temp: Temperatures, weather: Seq[Weather], speed: java.math.BigDecimal, deg: java.math.BigDecimal) {

  private val secondsInDay = 60 * 60 * 24

  private val directions = Array(
    "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"
  )

  val date = LocalDate.ofEpochDay(dt * secondsInDay)

  val day = date.format(DateTimeFormatter.ofPattern("EEEE"))

  val minimum = (BigDecimal(temp.min) - 273.15).setScale(1, RoundingMode.DOWN)

  val maximum = (BigDecimal(temp.max) - 273.15).setScale(1, RoundingMode.DOWN)

  val summary = s"${weather(0).main}, ${weather(0).description}"

  val icon = weather(0).icon

  val windSpeed = speed

  val windDirection = directions((BigDecimal(deg) / 22.5).setScale(0, RoundingMode.HALF_DOWN).toInt)
}

case class DailyForecast(city: City, list: Seq[Forecast]) {
  val forecasts = list
}

class WeatherService(host: String, baseUrl: String) {

  private val client = new SomethingOrNothingRestClient(host)

  def forecast(cityId: Int, numberOfDays: Int): Future[Option[DailyForecast]] = {
    val queryString = s"?id=$cityId&cnt=$numberOfDays&mode=json"
    client.get[DailyForecast](s"$baseUrl$queryString")
  }
}
