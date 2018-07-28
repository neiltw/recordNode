package models

import java.sql.Date
import java.util.Calendar

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class RecordDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider ) extends HasDatabaseConfigProvider[JdbcProfile]{

  import dbConfig.profile.api._


  private val records = TableQuery[recordTable]

  def findAll() : Future[Seq[Record]] = db.run(records.result)

  def findByMonth() = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val MonthStart = java.sql.Date.valueOf( cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH).+(1)+"-01")
    val MonthEnd = java.sql.Date.valueOf( cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH).+(1)+"-"+cal.get(Calendar.DATE))
    db.run( records.filter(p =>  {p.record_date >= MonthStart  &&  p.record_date <= MonthEnd } ).result )
  }

  def findByMonthGroupby() = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val MonthStart = java.sql.Date.valueOf( cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH).+(1)+"-01")
    val MonthEnd = java.sql.Date.valueOf( cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH).+(1)+"-"+cal.get(Calendar.DATE))
    db.run( records.filter(p =>  {p.record_date >= MonthStart  &&  p.record_date <= MonthEnd } ).groupBy(_.record_name)
      .map{ case (name,group) => (name , group.map(_.record_pay).sum) }.result
    )
  }


  def findById(id :Int) = db.run(
    records.filter(_.id ===id).result.headOption
  )

  def findByName(name:String) = {
    val findName = db.run(records.filter(_.record_name === name).result.headOption  )
    findName.map{
      result => result match {
        case Some(record) => record
        case None => throw new Exception("record not found")
      }
    }
  }

  def createRecord(name:String,date:Date,pay:Int,remark:String) = {
    val record = Record(0,name,pay,date,remark)
    db.run( ( records returning records.map(_.id) ) += record)
  }


  def update(record:Record):Future[Int] ={
    db.run( records.filter(_.id=== record.id)
        .map( r => (r.record_name,r.record_date,r.record_pay, r.remark))
        .update(record.record_name,record.record_date,record.record_pay, record.remark)
    )
  }

  class recordTable(tag:Tag) extends Table[Record](tag, "record"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def record_name = column[String]("record_name")

    def record_pay = column[Int]("record_pay")

    def record_date = column[Date]("record_date")

    def remark = column[String]("remark")

    def * = (id,record_name,record_pay,record_date, remark) <>( (Record.apply _).tupled, Record.unapply)
  }


}
