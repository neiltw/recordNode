package models

import java.sql.Date

import play.api.libs.json.Json

case class Expenditure(id:Int,month:Int,payDate:Date , amount:Int)

object Expenditure {
  implicit  val expenditureFormat = Json.format[Expenditure]
}
