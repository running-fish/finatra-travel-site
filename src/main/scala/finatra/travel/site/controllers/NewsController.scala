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
package finatra.travel.site.controllers

import finatra.travel.site.views.NewsPageView
import com.twitter.finatra.ContentType.{Html, Json}

class NewsController(secret: String)
  extends AuthController(secret) with NewsService {

  get("/news") {
    OptionalAuth {
      request => {
        newsService.news() flatMap {
          newsItems => {
            val view = NewsPageView.from(request.user, newsItems.take(8))
            log.debug(view.toString)
            respondTo(request) {
              case _:Json => render.json(view).toFuture
              case _:Html => render.view(view).toFuture
            }
          }
        }
      }
    }
  }
}

