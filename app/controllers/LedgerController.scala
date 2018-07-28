package controllers

import javax.inject.Inject
import models.{ExpenditureDAO, RecordDAO}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.LedgerService
import views.html.helper.CSRF

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class LedgerController  @Inject()(recordDAO:RecordDAO ,expenditureDAO:ExpenditureDAO,  cc: ControllerComponents) extends AbstractController(cc)  with I18nSupport {


  // show month pay data
  def ledgerIndex = Action.async { implicit rs =>
    Logger.info("ledger index")
    val token = CSRF.getToken(rs)
    val user:Boolean = rs.session.get("user").exists( name => true)
    if(user == false ){
      Logger.info("ledger index error")
      Future.successful( Redirect(routes.LoginController.login()).flashing("error" -> "not user") )
    }else{
      Logger.info("ledger index..")
      val result = new LedgerService().ShowIndex(recordDAO,expenditureDAO)
      result._1.map( get =>  Ok(views.html.ledger(get,result._2,result._3, result._4) ).withSession("user"-> user.toString ,"csrfToken"->token.value) )
    }
  }

  //get chart pie
  def ledgerChart = Action {
    Logger.info("Get Chart data")
    Ok(Json.toJson(Await.result(new LedgerService().GetLedgerChart(recordDAO),Duration.Inf)))
  }



  def changeMonth = Action { implicit  rs =>
    Logger.info("change month money")
    val month = rs.body.asFormUrlEncoded.get("month_data").head.toInt
    val amount = rs.body.asFormUrlEncoded.get("amount").head.toInt
    val monthId = rs.body.asFormUrlEncoded.get("month_id").head.toInt
    println(month,amount,monthId)
    new LedgerService().changeMonthPay(monthId,amount,month,expenditureDAO)
    Ok("")
  }


}
