package services

import java.util.Calendar

import models._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class LedgerService {

  def GetLedgerChart(recordDAO: RecordDAO):Future[Seq[(String, Option[Int])]] = {
    recordDAO.findByMonthGroupby()
  }

  def ShowIndex(recordDAO:RecordDAO, expenditureDAO: ExpenditureDAO):(Future[Seq[Record]], Int, Expenditure, Int)= {
    val GetLedger = recordDAO.findByMonth()
    var Pay_sum = 0
    val Total =  Await.result(recordDAO.findByMonth(), Duration.Inf)
    Total.foreach( Pay_sum +=_.record_pay)
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val GetNowMonthDate = java.sql.Date.valueOf( cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH).+(1)+"-01")
//    println(GetNowMonthDate)
    val monthAmount :Expenditure = Await.result(expenditureDAO.findByDate(GetNowMonthDate), Duration.Inf)
    val surplus_cash = monthAmount.amount - Pay_sum

    (GetLedger,Pay_sum,monthAmount,surplus_cash)
  }

  def changeMonthPay(id:Int,amount:Int , month:Int ,expenditureDAO: ExpenditureDAO) {
    val date = Calendar.getInstance()

    println(id , amount,month)
  }

}
