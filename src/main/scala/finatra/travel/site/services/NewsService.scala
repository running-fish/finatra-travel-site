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

import com.twitter.util.Future
import com.twitter.finagle.HttpClient
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpVersion, DefaultHttpRequest, HttpRequest}
import com.twitter.logging.Logger
import com.twitter.finatra.config
import java.io.InputStream
import scala.collection.mutable.ListBuffer
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.digester3.Digester
import scala.beans.BeanProperty
import org.jboss.netty.buffer.ChannelBufferInputStream

case class NewsItem(headline: String, standFirst: String, link: String, image: String)

class NewsService(host: String, baseUrl: String) {

  private val log = Logger(config.logNode())

  private val service = HttpClient.newClient(host).toService

  private val scalaDigester = new ScalaDigester

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
            try {
              val list = scalaDigester.from(new ChannelBufferInputStream(body)).map(_.asNewsItem()).toList
              log.info("News:" + list)
              list
            } catch {
              case e: Exception => {
                log.error(e, "Failed reading news feed")
                List.empty
              }
            }
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

class ScalaDigester {

  def from(inputStream: InputStream): ListBuffer[NewsItemDigestible] = {
    val digester = new Digester()
    digester.setValidating(false)
    digester.push(ListBuffer[NewsItemDigestible]())
    digester.addObjectCreate("rss/channel/item", classOf[NewsItemDigestible].getName)
    digester.addSetNext("rss/channel/item", "$plus$eq")
    digester.addBeanPropertySetter("rss/channel/item/title", "headline")
    digester.addBeanPropertySetter("rss/channel/item/description", "standFirst")
    digester.addBeanPropertySetter("rss/channel/item/guid", "link")
    digester.addObjectCreate("rss/channel/item/media:content", classOf[Image].getName)
    digester.addSetProperties("rss/channel/item/media:content")
    digester.addSetNext("rss/channel/item/media:content", "addImage")
    digester.parse(inputStream)
  }
}

// some java bean style classes for Digester to use :)
class Image(@BeanProperty var width: Int, @BeanProperty var height: Int, @BeanProperty var url: String) {

  def this() {
    this(0, 0, "")
  }

  def isLargerThan(other: Image) = {
    this.height > other.height && this.width > other.width
  }

  override def toString = s"Image($width, $height, $url)"
}

class NewsItemDigestible(@BeanProperty var headline: String,
                              @BeanProperty var standFirst: String,
                              @BeanProperty var link: String) {

  def this() {
    this("", "", "")
  }

  override def toString = s"NewsItemDigestible($headline, $standFirst, $link, $images)"

  private val images: ListBuffer[Image] = ListBuffer()

  def addImage(image: Image) {
    images += image
  }

  def asNewsItem(): NewsItem = {
    NewsItem(headline, formatStandFirst(), link, firstLargestImage().url)
  }

  private def formatStandFirst(): String = {
    var unescaped = StringEscapeUtils.unescapeHtml(standFirst)
    if (unescaped.startsWith("<p>")) {
      unescaped = unescaped.substring(3, unescaped.indexOf('<', 3))
    } else {
      unescaped = unescaped.substring(0, unescaped.indexOf('<'))
    }
    normaliseWhitespace(unescaped).trim
  }

  private def normaliseWhitespace(s: String): String = {
    s.replaceAll("\\s+", " ")
  }

  private def firstLargestImage(): Image = {
    images.sortWith(sortBySize).head
  }

  private def sortBySize: (Image, Image) => Boolean = (a, b) => a.isLargerThan(b)
}
