package models

import play.api.libs.json.Json


case class Users(id:Int,user:String,password:String,role:String)

object Users {
  implicit val userFormat =  Json.format[Users]
}


