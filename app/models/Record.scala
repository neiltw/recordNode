package models

import java.sql.Date

import play.api.libs.json.Json

case class Record(id:Int,record_name:String,record_pay:Int,record_date:Date, remark:String)

object Record {
  implicit val recordFormat = Json.format[Record]
}
