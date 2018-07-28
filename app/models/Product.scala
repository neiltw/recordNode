package models

import play.api.libs.json.Json


case class Product(id:Int, product_name:String, category_id:Int)


object Product {
  implicit val productFormat = Json.format[Product]

}

