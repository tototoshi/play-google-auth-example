package controllers

import jakarta.inject.Inject
import jakarta.inject.Singleton
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

@Singleton
class HomeController @Inject() (
    cc: ControllerComponents,
    authorizedAction: AuthorizedAction
) extends AbstractController(cc) {

  def index: Action[AnyContent] = Action.andThen(authorizedAction) { implicit request =>
    Ok(views.html.index(request.account.email))
  }

}
