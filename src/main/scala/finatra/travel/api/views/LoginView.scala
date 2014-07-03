package finatra.travel.api.views

import com.twitter.finatra.View

class LoginView(val error: Option[String] = None) extends View {

  def template: String = "login.mustache"
}
