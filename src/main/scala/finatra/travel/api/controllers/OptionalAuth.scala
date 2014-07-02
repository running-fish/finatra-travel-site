package finatra.travel.api.controllers

import com.twitter.finagle.http.{Request => FinagleRequest}
import com.twitter.finatra.Request
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.Future

case class User(id: String)

class OptionalUserRequest(request: FinagleRequest, val user: Option[User]) extends Request(request) {
}

object OptionalAuth {
  def apply(action: OptionalUserRequest => Future[ResponseBuilder]): Request => Future[ResponseBuilder] = {
    request => {
      // TEMP: get user id from request query string ...
      // TODO: replace this with "session" cookie and UserService, like the play version
      val user = request.params.get("id") map {
        id => User(id)
      }
      action(new OptionalUserRequest(request, user))
    }
  }
}
