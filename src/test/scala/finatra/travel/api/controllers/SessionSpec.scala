package finatra.travel.api.controllers

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import com.twitter.finagle.http.{Cookie, CookieMap}
import org.mockito.Mockito._

class SessionSpec extends FlatSpec with ShouldMatchers with Session with MockitoSugar {

  "Session" should "validate signed cookie" in {
    val secret = "09vwi985e(*ht430)*!(T)JP"
    val id = "945029340236"

    val cookie = toCookie(secret, id)

    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(cookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(Some(id))
  }

  it should "reject an empty encoded value" in {
    val secret = "09vwi985e(*ht430)*!(T)JP"
    val id = "945029340236"

    val cookie = toCookie(secret, id)

    val value = cookie.value
    val emptyEncoded = value.substring(value.indexOf('|'))

    val emptyEncodedCookie = new Cookie(COOKIE_NAME, emptyEncoded)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(emptyEncodedCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject an empty timestamp value" in {
    val secret = "09vwi985e(*ht430)*!(T)JP"
    val id = "945029340236"

    val cookie = toCookie(secret, id)

    val value = cookie.value
    val firstPipe = value.indexOf('|')
    val secondPipe = value.indexOf('|', firstPipe + 1)
    val emptyTimestamp = value.substring(0, firstPipe) + "|" + value.substring(secondPipe)

    val emptyTimestampCookie = new Cookie(COOKIE_NAME, emptyTimestamp)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(emptyTimestampCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject a value with only two elements" in {
    val secret = "09vwi985e(*ht430)*!(T)JP"
    val id = "945029340236"

    val cookie = toCookie(secret, id)

    val value = cookie.value
    val firstPipe = value.indexOf('|')
    val secondPipe = value.indexOf('|', firstPipe + 1)
    val missingTimestamp = value.substring(0, firstPipe) + value.substring(secondPipe)

    val missingTimestampCookie = new Cookie(COOKIE_NAME, missingTimestamp)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(missingTimestampCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject an invalid timestamp" in {
    val secret = "09vwi985e(*ht430)*!(T)JP"
    val id = "945029340236"

    val cookie = toCookie(secret, id)

    val value = cookie.value
    val firstPipe = value.indexOf('|')
    val secondPipe = value.indexOf('|', firstPipe + 1)
    val invalidTimestamp = value.substring(0, firstPipe) + "|109f8e5w9fw" + value.substring(secondPipe)

    val invalidTimestampCookie = new Cookie(COOKIE_NAME, invalidTimestamp)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(invalidTimestampCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject a missing signature" in {
    val secret = "09vwi985e(*ht430)*!(T)JP"
    val id = "945029340236"

    val cookie = toCookie(secret, id)

    val value = cookie.value
    val firstPipe = value.indexOf('|')
    val secondPipe = value.indexOf('|', firstPipe + 1)
    val missingSignature = value.substring(0, secondPipe) + "|"

    val missingSignatureCookie = new Cookie(COOKIE_NAME, missingSignature)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(missingSignatureCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }

  it should "reject an invalid signature" in {
    val secret = "09vwi985e(*ht430)*!(T)JP"
    val id = "945029340236"

    val cookie = toCookie(secret, id)

    val value = cookie.value + "f"

    val invalidSignatureCookie = new Cookie(COOKIE_NAME, value)
    val cookieMap = mock[CookieMap]
    when(cookieMap.get(COOKIE_NAME)).thenReturn(Some(invalidSignatureCookie))

    val decoded = fromCookies(secret, cookieMap)
    decoded should be(None)
  }
}
