package finatra.travel.site.controllers

import com.twitter.finagle.http.{Cookie, CookieMap}

trait TestSession extends Session {

  override def fromCookies(secret:String, cookies: CookieMap): Option[String] = {
    Some("100")
  }

  override def toCookie(secret: String, id: String): Cookie = {
    new Cookie(COOKIE_NAME, id)
  }

}
