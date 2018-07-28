package models

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



class ProductDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider ) extends HasDatabaseConfigProvider[JdbcProfile]{



  import dbConfig.profile.api._

  private val product = TableQuery[productTable]

  class productTable(tag:Tag) extends Table[Product](tag, "product"){
    def id = column[Int]("id",O.PrimaryKey,O.AutoInc)

    def product_name = column[String]("product_name")

    def category_id = column[Int]("category_id")

    def * = (id,product_name, category_id) <> ( (Product.apply _).tupled, Product.unapply)

  }

  def findAll() : Future[Seq[Product]] = db.run(product.result)

  def findById(id:Int)  = db.run(
    product.filter(_.id === id ).result.headOption
  )

  def findByName(name:String):Future[Product] = {
    val findName = db.run(product.filter(_.product_name === name).result.headOption)
    findName.map {
      result =>
        result match {
        case Some(n) => n
        case None => new Product(-1,null,0)
      }
    }
  }

  def createProduct(product_name:String, category_id:Int) = {
    val insertProduct = Product(0,product_name,category_id)
    db.run(product returning product.map(_.id) += insertProduct)
  }

  def update(p:Product):Future[Int] ={
    db.run( product.filter(_.id=== p.id)
        .map(p => (p.product_name))
        .update(p.product_name)
    )
  }

  def delete(id:Int): Future[Int] = {
    db.run(product.filter(_.id === id).delete )
  }



}
