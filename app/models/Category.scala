package models

import play.api.libs.json.Json

case class Category(category_id:Int,category_name:String)

object Category {
  implicit val categoryFormat = Json.format[Category]
}
