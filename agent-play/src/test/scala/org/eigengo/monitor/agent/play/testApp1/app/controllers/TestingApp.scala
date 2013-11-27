package org.eigengo.monitor.agent.play.testApp1.app.controllers

import play.api.mvc.{Action, Controller}


object TestingApp extends Controller {
  def index = Action {
    Ok(views.html.index.render("Hello Play Framework"))
  }
}

