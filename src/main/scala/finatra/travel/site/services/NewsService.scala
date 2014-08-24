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

import java.io.InputStream

import com.twitter.finagle.HttpClient
import com.twitter.finatra.config
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.apache.commons.lang.StringEscapeUtils
import org.jboss.netty.buffer.ChannelBufferInputStream
import org.jboss.netty.handler.codec.http.{DefaultHttpRequest, HttpMethod, HttpVersion}

import scala.xml.XML

case class Image(width: Int, height: Int, url: String) {
  def isLargerThan(other: Image) = {
    this.height > other.height && this.width > other.width
  }
}

case class NewsItem(headline: String, description: String, link: String, images: Seq[Image]) {

  val standFirst = {
    var unescaped = StringEscapeUtils.unescapeHtml(description)
    if (unescaped.startsWith("<p>")) {
      unescaped = unescaped.substring(3, unescaped.indexOf('<', 3))
    } else {
      unescaped = unescaped.substring(0, unescaped.indexOf('<'))
    }
    unescaped.replaceAll("\\s+", " ").trim
  }

  val image = images.sortWith((a, b) => a.isLargerThan(b)).head.url
}

class NewsService(host: String, baseUrl: String) {

  private val log = Logger(config.logNode())

  private val service = HttpClient.newClient(host).toService

  private val parser = new GruaniadNewsParser

  def news(): Future[List[NewsItem]] = {
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, baseUrl)
    request.headers().add("Accept", "application/xml")
    request.headers().add("Host", host)
    log.info(s"NewsService executing $request")
    service(request) map {
      response => {
        log.info(s"NewsService response status ${response.getStatus}")
        response.getStatus.getCode match {
          case 200 => {
            val body = response.getContent
            val stream = new ChannelBufferInputStream(body)
            val list = parser.parse(stream).toList
            log.info("News:" + list)
            list
          }
          case _ => List.empty
        }
      }
    } rescue {
      case e => {
        log.error(e, "Failed retrieving rss feed")
        Future.value(List.empty)
      }
    }
  }
}

class GruaniadNewsParser {

  def parse(stream: InputStream): Seq[NewsItem] = {
    val xml = XML.load(stream)
    (xml \\ "item").map {
      item => {
        val title = (item \ "title").text.trim
        val description = (item \ "description").text.trim
        val link = (item \ "guid").text.trim
        val images: Seq[Image] = (item \ "content").map {
          image => {
            val width = (image \ "@width").text.toInt
            val height = (image \ "@height").text.toInt
            val url = (image \ "@url").text
            Image(width, height, url)
          }
        }
        NewsItem(title, description, link, images)
      }
    }
  }
}
