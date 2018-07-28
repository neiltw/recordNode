package services

import java.sql.Date

import models.{Expenditure, ExpenditureDAO}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SpendService {

  def createBudget( expenditureDAO: ExpenditureDAO , month:Int,PayDate:Date, amount:Int){
    expenditureDAO.add(month,PayDate,amount)
  }

  def updateBudget(expenditureDAO: ExpenditureDAO , month:Int,PayDate:Date, amount:Int):Int = {
    Await.result( expenditureDAO.update(month,PayDate,amount),Duration.Inf )
  }

  def searchBudget(expenditureDAO: ExpenditureDAO , month:Int,PayDate:Date, amount:Int):Seq[Expenditure] = {
    Await.result( expenditureDAO.findByPayDate(PayDate),Duration.Inf )
  }

}
