package controllers

import assets.ActiveClient
import play.api.mvc._
import play.api.Play.current


object Application extends Controller {

  def socket = WebSocket.acceptWithActor[String, String] { request => out =>
    ActiveClient.props(out)
  }

  def index = Action {
    Ok(views.html.index())
  }

}