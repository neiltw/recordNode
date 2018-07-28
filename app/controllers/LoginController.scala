package controllers

import javax.inject.Inject

import models.{Users, UsersDAO}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{default, mapping, number, text}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class LoginController  @Inject() ( userDAO:UsersDAO, cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {



  val userForm = Form(
    mapping(
      "id" -> default(number,1),
      "user" -> text,
      "password" -> text,
      "role" -> default(text,"user")
    )(Users.apply)(Users.unapply)
      verifying("Failed form constraints!", fields => fields match {
      case user => userDAO.checkUserOrPassword(user.user,user.password)
    })
  )

  def index = Action{ implicit  rs =>
    Redirect(routes.LoginController.login())
  }


  def login = Action { implicit  rs =>
    Ok(views.html.login(userForm))
  }

  def logout = Action{ implicit  rs =>
    Redirect(routes.LoginController.login).withNewSession
      .flashing( "success" -> "You are now logged out.")
  }


  /**
    * check user and password is true
    * */

  def authenticate = Action{  implicit rs =>
    userForm.bindFromRequest.fold(
      formWithErrors => { Logger.info("Login Error ");BadRequest(views.html.login(formWithErrors)) },
      user =>  { Logger.info("Login Success "); Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> user.user)  }
    )
  }

  /**
    * test Get databases to Json
    * */
  def User = Action.async{
    val user:Future[Seq[Users]] = userDAO.findAll()
    user.map{
      userData => Ok(Json.toJson(userData))
    }
  }






}
