package models


import java.sql.Date

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExpenditureDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider ) extends HasDatabaseConfigProvider[JdbcProfile]{

  import dbConfig.profile.api._

  private val expenditure = TableQuery[expenditureTable]

  class expenditureTable (tag:Tag ) extends Table[Expenditure](tag,"expenditure"){

    def id = column[Int]("id",O.PrimaryKey,O.AutoInc)

    def month = column[Int]("month")

    def payDate = column[Date]("payDate")

    def amount = column[Int]("amount")

    def * = (id , month , payDate , amount) <> ( ( Expenditure.apply _).tupled , Expenditure.unapply )
  }


  def findByDate(date:Date):Future[Expenditure]= {
    val findName = db.run(expenditure.filter(_.payDate === date).result.headOption  )
    findName.map{
      result => result match {
        case Some(record) => record
        case None => Expenditure.apply(1,date.toLocalDate.getMonthValue,date,0)
      }
    }
  }

  def add(month:Int, PayDate:Date , amount:Int ) = {
    val Spend = Expenditure(0,month,PayDate,amount)
    db.run(
      expenditure returning expenditure.map(_.id) += Spend
    )
  }

  def update(month:Int , PayDate:Date,amount:Int):Future[Int]={
    db.run( expenditure.filter(_.payDate === PayDate)
      .map(p => (p.month,p.payDate,p.amount))
      .update(month,PayDate, amount)
    )
  }

  def findByPayDate( PayDate:Date):Future[Seq[Expenditure]]={
    db.run( expenditure.filter(_.payDate === PayDate).result )
  }


}


