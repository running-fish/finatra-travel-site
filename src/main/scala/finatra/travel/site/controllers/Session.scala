package finatra.travel.site.controllers

import org.jboss.netty.util.CharsetUtil
import com.twitter.finagle.http.{CookieMap, Cookie}
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac
import java.net.{URLDecoder, URLEncoder}
import scala.util.control.NonFatal

/**
 * Derived from Play's CookieBaker
 */
trait Session {

  val COOKIE_NAME = "GETAWAY_SESSION"

  val signer = new Signer
  val verifier = new Verifier(signer)
  val cookieEncoder = new CookieEncoder(signer)
  val cookieDecoder = new CookieDecoder(verifier)

  def fromCookies(secret:String, cookies: CookieMap): Option[String] = {
    cookies.get(COOKIE_NAME) flatMap {
      cookie => cookieDecoder.decode(secret, cookie.value).get("id")
    }
  }

  def toCookie(secret: String, id: String): Cookie = {
    val encoded = cookieEncoder.encode(secret, Map("id" -> id))
    new Cookie(COOKIE_NAME, encoded)
  }
}

class Signer {

  val HMAC_SHA1_ALGORITHM = "HmacSHA1"

  val hexChars = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

  def sign(key: String, data: String) = {
    val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
    mac.init(new SecretKeySpec(key.getBytes(CharsetUtil.UTF_8), HMAC_SHA1_ALGORITHM))
    new String(toHex(mac.doFinal(data.getBytes(CharsetUtil.UTF_8))))
  }

  def toHex(array: Array[Byte]): Array[Char] = {
    val result = new Array[Char](array.length * 2)
    for (i <- 0 until array.length) {
      val b = array(i) & 0xff
      result(2 * i) = hexChars(b >> 4)
      result(2 * i + 1) = hexChars(b & 0xf)
    }
    result
  }
}

class CookieEncoder(signer: Signer) {

  def encode(key: String, values: Map[String, String]) = {
    val encoded = values.map {
      case (k, v) => URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8")
    }.mkString("&")

    val signature = signer.sign(key, encoded);
    signature + "-" + encoded
  }
}

class Verifier(signer: Signer) {

  def verify(key: String, data: String, signature: String) = {
    val calculatedSignature = signer.sign(key, data)
    compare(signature, calculatedSignature)
  }

  private def compare(a: String, b: String) = {
    if (a.length != b.length) {
      false
    } else {
      var equal = 0
      for (i <- Array.range(0, a.length)) {
        equal |= a(i) ^ b(i)
      }
      equal == 0
    }
  }
}

class CookieDecoder(verifier: Verifier) {

  private def urlDecode(data: String) = {
    data
      .split("&")
      .map(_.split("=", 2))
      .map(p => URLDecoder.decode(p(0), "UTF-8") -> URLDecoder.decode(p(1), "UTF-8"))
      .toMap
  }

  def decode(key: String, data: String): Map[String, String] = {
    try {
        val split = data.split("-", 2)
        val message = split.tail.mkString("-")
        if (verifier.verify(key, message, split(0))) {
          urlDecode(message)
        } else {
          Map.empty[String, String]
        }
    } catch {
      case NonFatal(_) => Map.empty[String, String]
    }
  }
}
