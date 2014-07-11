package finatra.travel.api.controllers

import java.util.Base64
import org.jboss.netty.util.CharsetUtil
import com.twitter.finagle.http.{CookieMap, Cookie}
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

/**
 * Derived from https://github.com/mdennebaum/golden-arm/blob/master/src/main/scala/com/twitter/GoldenArm/utils/AuthUtils.scala
 */
trait Session {

  val COOKIE_NAME = "GETAWAY_SESSION"

  def fromCookies(secret:String, cookies: CookieMap): Option[String] = {
    cookies.get(COOKIE_NAME) flatMap {
      cookie => {
        cookie.value.split("\\|") match {
          case Array(encoded, timestamp, signature) if valid(encoded, timestamp, signature) => {
            if (signaturesMatch(secret, encoded, timestamp, signature)) {
              try {
                Some(new String(Base64.getDecoder.decode(encoded.getBytes(CharsetUtil.UTF_8)), CharsetUtil.UTF_8))
              } catch {
                case iae: IllegalArgumentException => None
              }
            } else {
              None
            }
          }
          case _ => None
        }
      }
    }
  }

  def toCookie(secret: String, id: String): Cookie = {
    val timestamp = System.currentTimeMillis
    val encoded = new String(Base64.getEncoder.encode(id.getBytes(CharsetUtil.UTF_8)), CharsetUtil.UTF_8)
    val signature = createSignature(secret, encoded, timestamp.toString)
    new Cookie(COOKIE_NAME, encoded + "|" + timestamp.toString + "|" + signature)
  }

  // TODO: check encoded is valid Base64 alphabet?
  // then the catch IllegalArgumentException will be unnecessary?
  private def valid(encoded: String, timestamp: String, signature: String): Boolean = {
    !encoded.isEmpty && !timestamp.isEmpty && timestamp.forall(_.isDigit) && !signature.isEmpty
  }
  
  private def signaturesMatch(secret: String, encoded: String, timestamp: String, signature: String): Boolean = {
    val check = createSignature(secret, encoded, timestamp.toString)
    compare(signature.getBytes(CharsetUtil.UTF_8), check.getBytes(CharsetUtil.UTF_8))
  }

  private def createSignature(secret: String, encoded: String, timestamp: String) = {
    val specSecret = new SecretKeySpec(secret.getBytes, "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(specSecret)
    mac.update(encoded.getBytes(CharsetUtil.UTF_8))
    mac.update(timestamp.getBytes(CharsetUtil.UTF_8))
    mac.doFinal().mkString
  }

  private def compare(a: Array[Byte], b: Array[Byte]) = {
    if (a.length != b.length) {
      false
    } else {
      (0 until a.length).foldLeft(0) {
        (acc, i) => acc | (a(i) ^ b(i))
      } == 0
    }
  }
}
