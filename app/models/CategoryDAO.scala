package models


import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.Future
import slick.jdbc.JdbcProfile

class CategoryDAO  @Inject() (protected val dbConfigProvider: DatabaseConfigProvider ) extends HasDatabaseConfigProvider[JdbcProfile]{

  import dbConfig.profile.api._

  private val category = TableQuery[categoryTable]

  class categoryTable(tag:Tag) extends Table[Category](tag,"product_category"){
    def category_id = column[Int]("category_id",O.PrimaryKey,O.AutoInc)

    def category_name = column[String]("category_name")

    def * = (category_id, category_name) <> ( (Category.apply _).tupled , Category.unapply )
  }


  def findAll(): Future[Seq[Category]] = db.run( category.result )

  def finByid(id:Int) = db.run(
    category.filter(_.category_id === id).result.headOption
  )

  def createCategory(category_id:Int,category_name:String) = db.run{
    ( category.map( c => (c.category_id , c.category_name))
      returning category.map(_.category_id)
      into ( (name,id) => Category(category_id,category_name))
    ) +=(category_id,category_name)
  }

  def updateCategory(c:Category) = db.run(
    category.filter(_.category_id === c.category_id)
      .map( c =>( c.category_name))
      .update(c.category_name)
  )

}
