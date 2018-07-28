package controllers

import java.util.Calendar

import javax.inject.Inject
import models._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{default, mapping, number, sqlDate, text}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import services.SpendService
import views.html.helper.CSRF

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



class LiftRecordController  @Inject()(productDAO: ProductDAO, recordDAO: RecordDAO,expenditureDAO: ExpenditureDAO, cc: ControllerComponents) extends AbstractController(cc)  with I18nSupport {


  /**
    * record page
    * */
  def LiftRecordIndex = Action.async { implicit rs =>
    Logger.info("Lift Record index")
    val token = CSRF.getToken(rs)
    val user:Boolean = rs.session.get("user").exists( name => true)
    val listProduct:Future[Seq[Product]] = productDAO.findAll()
    if(user == false ){
      Future.successful( Redirect(routes.LoginController.login()) )
    }else{
      Logger.info(" lift Record Index Page .....")
      val year = Calendar.getInstance.get(Calendar.YEAR)
      var budgetMonth = Seq[Map[String,Int]]()
      for(month <- 1 to 12){
        budgetMonth = budgetMonth.:+(Map(year+"-"+month+"-01"-> month) )
      }
//      println(budgetMonth)
      listProduct.map( get=> Ok(views.html.spend(get , budgetMonth) ).withSession("user"-> user.toString ,"csrfToken"->token.value) )
    }
  }

  val recordForm = Form(
    mapping(
      "id" -> default(number,1),
      "recordProduct" -> text ,
      "recordNumber" -> number,
      "recordDate" -> sqlDate,
      "recordRemark" -> text
    )(Record.apply) (Record.unapply)
  )

  val budgetForm = Form(
    mapping(
      "id" -> default(number,1),
      "month" -> number,
      "payDate" -> sqlDate,
      "amount" -> number
    )(Expenditure.apply) (Expenditure.unapply)
  )


  /**
    * add record data
    * */
  def addSpendProduct = Action.async{ implicit  rs =>
    val token = CSRF.getToken(rs)
    val user: Boolean = rs.session.get("user").exists(name => true)
    if (user == false) {
      Future.successful(Redirect(routes.LoginController.login()))
    }else{
      recordForm.bindFromRequest.fold(
        Error => {
          Logger.info("error data...")
          val flashData = "Error record"
          Future.successful(Redirect(routes.LiftRecordController.LiftRecordIndex()   ).flashing("recordError" -> flashData) )
        },
        GetRecord => {
          Logger.info("add ....")
          recordDAO.createRecord(GetRecord.record_name,GetRecord.record_date,GetRecord.record_pay,GetRecord.remark)
          val flashData = "Success"
          Future.successful(Redirect(routes.LiftRecordController.LiftRecordIndex() ).flashing("recordSuccess"-> flashData)  )

        }
      )
    }
  }


  def addMonthBudget = Action.async{ implicit rs =>
    Logger.info("Lift add Month Budget index")
    val token = CSRF.getToken(rs)
    val user:Boolean = rs.session.get("user").exists( name => true)
    if(user == false ){
      Future.successful( Redirect(routes.LoginController.login()) )
    }else{
      budgetForm.bindFromRequest.fold(
        Error => {
          Logger.info("error data...")
          val flashData = "Error budget"
          Future.successful(Redirect(routes.LiftRecordController.LiftRecordIndex()   ).flashing("budgetError" -> Error.errors.head.message) )
        },
        addBudget => {
          Logger.info("add budget ...")
          val year = Calendar.getInstance.get(Calendar.YEAR)
          val month = addBudget.month
          val day = "01"
          val toDay = java.sql.Date.valueOf(year+"-"+month+"-"+day)
          val isExist = new SpendService().searchBudget(expenditureDAO, addBudget.month,toDay, addBudget.amount)
          if(isExist.length > 0){
            new SpendService().updateBudget(expenditureDAO,addBudget.month,toDay,addBudget.amount)
          }else{
            new SpendService().createBudget(expenditureDAO,addBudget.month,toDay,addBudget.amount)
          }
          val flashData = "Success"
          Future.successful( Redirect(routes.LiftRecordController.LiftRecordIndex() ).flashing("budgetSuccess" -> flashData) )
        }
      )
    }
  }



}
