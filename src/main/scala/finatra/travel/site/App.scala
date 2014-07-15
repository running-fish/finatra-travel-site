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
package finatra.travel.site

import com.twitter.finatra._
import finatra.travel.site.controllers.{OffersController, LoginController, HomeController}

object App extends FinatraServer {

  // temporary hack
  System.setProperty("com.twitter.finatra.config.port", ":9000")

  private val applicationSecret = flag("applicationSecret",
    "3Aq1o?MUpq0IN>/oG^8DrWaPEKYBx5hbc]CrvO/n@=2?cKakD82[Ofgh8bokHQV?", "The secret used for cookie signing")

  register(new HomeController(applicationSecret()))
  register(new LoginController(applicationSecret()))
  register(new OffersController(applicationSecret()))
}
