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

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class OffersServiceSpec extends FlatSpec with ShouldMatchers {

  val offersService = new OffersService("localhost:9101", "/offers")

  "Offers Service query string builder" should "build empty query string with no profile or loyalty" in {
    offersService.queryString(None, None) should be("?")
  }

  it should "build query string containing only loyalty properties" in {
    offersService.queryString(None, Some(Loyalty("bronze", 205))) should be("?loyalty=bronze")
  }

  it should "build query string containing only profile properties" in {
    offersService.queryString(Some(Profile("prefamily", "standard", "female")), None) should
      be("?lifecycle=prefamily&spending=standard&gender=female")
  }

  it should "build query string containing both profile and loyalty properties" in {
    offersService.queryString(
      Some(Profile("prefamily", "standard", "female")),
      Some(Loyalty("silver", 8444))) should
      be("?lifecycle=prefamily&spending=standard&gender=female&loyalty=silver")
  }
}
