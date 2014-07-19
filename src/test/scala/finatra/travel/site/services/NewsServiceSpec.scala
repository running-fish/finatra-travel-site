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

class NewsServiceSpec extends FlatSpec with ShouldMatchers with WireMockSupport {

  val newsService = new NewsService("localhost:9101", "/news")

  private def stubNews(url: String) {
    val inputStream: InputStream = getClass.getResourceAsStream("/grauniad-travel-news.xml")
    val stubData = Source.fromInputStream(inputStream).mkString("")
    stubGet(url, stubData)
    inputStream.close()
  }

  "News Service" should "read the news" in {
    stubNews("/news");

    val futureNews = newsService.news()
    val news = Await.result(futureNews, Duration.fromSeconds(3))

    news should have size(2)

    val first: NewsItem = news(0)
    first.headline should be("Londons first board game cafe to open in Hackney")
    first.standFirst should be("Draughts, the capitals latest concept cafe, hopes to capitalise on a new trend" +
      " for beer and board games, with over 500 different tabletop games on offer")
    first.link should be("http://www.theguardian.com/travel/2014/jul/16/" +
      "london-first-board-game-cafe-to-open-in-hackney")
    first.image should be("http://static.guim.co.uk/sys-images/Guardian/Pix/pictures/" +
      "2014/7/16/1405502998989/e83ad4cc-ec6b-45c7-9270-79b37d905742-460x276.jpeg")

    val second: NewsItem = news(1)
    second.headline should be ("Travel tips: Oslos trendy new suburb Grünerløkka, and the weeks best deals")
    second.standFirst should be("Edgy and urban, this once gritty corner of the Norwegian capital has been given" +
      " a new lease of life. Plus, beach huts in Devon and cheap villas in Ibiza")
    second.link should be("http://www.theguardian.com/travel/2014/jul/13/travel-tips-oslo-devon-ibiza-joanne-oconnor")
    second.image should be("http://static.guim.co.uk/sys-images/Guardian/Pix/pictures/" +
      "2014/7/9/1404922550268/09c1c517-bde3-4edc-adf6-0f0deb01b419-460x276.jpeg")
  }
}

