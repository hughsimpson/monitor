package controllers

import play.api.mvc.{Action, Controller}


object TestingApp extends Controller {
  def index = Action {
    Ok(views.html.index.render("Hello Play Framework"))
  }
}

