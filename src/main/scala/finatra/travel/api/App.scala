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
package finatra.travel.api

import com.twitter.finatra._
import finatra.travel.api.controllers.{LoginController, HomeController}
import finatra.travel.api.services._

object App extends FinatraServer {

  // temporary hack
  System.setProperty("com.twitter.finatra.config.port", ":9000")

  private val applicationSecret = flag("applicationSecret",
    "woiegjv*j49ux^gew9)ijew,@-,mweHE9d(&dr3$", "The secret used for cookie signing")

  register(new HomeController(applicationSecret()))
  register(new LoginController(applicationSecret()))
}
