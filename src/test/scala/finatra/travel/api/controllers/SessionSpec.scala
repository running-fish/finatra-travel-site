package finatra.travel.api.controllers

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import com.twitter.finagle.http.{Cookie, CookieMap}
import org.mockito.Mockito._

class SessionSpec extends FlatSpec with ShouldMatchers with Session with MockitoSugar {

  val secret = "09vwi985e(*ht430)*!(T)JP"
  val id = "945029340236"
  val cookie = toCookie(secret, id)
  val cookieValue = cookie.value

  "Session" should "validate signed cookie" in {
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(cookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(Some(id))
  }

  it should "reject an empty encoded value" in {
    val emptyEncoded = cookieValue.substring(cookieValue.indexOf('|'))

    val emptyEncodedCookie = new Cookie(COOKIE_NAME, emptyEncoded)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(emptyEncodedCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject an empty timestamp value" in {
    val firstPipe = cookieValue.indexOf('|')
    val secondPipe = cookieValue.indexOf('|', firstPipe + 1)
    val emptyTimestamp = cookieValue.substring(0, firstPipe) + "|" + cookieValue.substring(secondPipe)

    val emptyTimestampCookie = new Cookie(COOKIE_NAME, emptyTimestamp)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(emptyTimestampCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject a value with only two elements" in {
    val firstPipe = cookieValue.indexOf('|')
    val secondPipe = cookieValue.indexOf('|', firstPipe + 1)
    val missingTimestamp = cookieValue.substring(0, firstPipe) + cookieValue.substring(secondPipe)

    val missingTimestampCookie = new Cookie(COOKIE_NAME, missingTimestamp)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(missingTimestampCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject an invalid timestamp" in {
    val firstPipe = cookieValue.indexOf('|')
    val secondPipe = cookieValue.indexOf('|', firstPipe + 1)
    val invalidTimestamp = cookieValue.substring(0, firstPipe) + "|109f8e5w9fw" + cookieValue.substring(secondPipe)

    val invalidTimestampCookie = new Cookie(COOKIE_NAME, invalidTimestamp)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(invalidTimestampCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject a missing signature" in {
    val firstPipe = cookieValue.indexOf('|')
    val secondPipe = cookieValue.indexOf('|', firstPipe + 1)
    val missingSignature = cookieValue.substring(0, secondPipe) + "|"

    val missingSignatureCookie = new Cookie(COOKIE_NAME, missingSignature)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(missingSignatureCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject an invalid signature" in {
    val invalidSignatureCookie = new Cookie(COOKIE_NAME, cookieValue + "f")
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(invalidSignatureCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }
}
