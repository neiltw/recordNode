package controllers

import javax.inject.Inject
import models.{CategoryDAO, Product, ProductDAO, UsersDAO}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{default, mapping, number, text}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import views.html.helper.CSRF

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class CategoryController  @Inject()(productDAO: ProductDAO, usersDAO: UsersDAO, categoryDAO :CategoryDAO , cc: ControllerComponents) extends AbstractController(cc)  with I18nSupport {

  val ProductForm = Form(
    mapping(
      "id" -> default(number,1),
      "product_name" -> text ,
      "category_id" -> number
    )(Product.apply) (Product.unapply)
  )


  /**
    * category page
    * */
  def CategoryIndex = Action.async { implicit rs =>
    Logger.info("Lift Record index")
    val token = CSRF.getToken(rs)
    val user:Boolean = rs.session.get("user").exists( name => true)
    val listProduct:Future[Seq[Product]] = productDAO.findAll()
    if(user == false ){
      Future.successful( Redirect(routes.LoginController.login()) )
    }else{
      val product:Product = Await.result(productDAO.findByName("") , Duration.Inf )
      val category = Await.result(categoryDAO.findAll(), Duration.Inf)
      Logger.info(" lift Record Index Page .....")
      listProduct.map( name=> Ok(views.html.category(ProductForm,name,product,category)).withSession("user"-> user.toString ,"csrfToken"->token.value) )
    }
  }

  /**
    * create product
    * */
  def createProduct = Action.async { implicit  rs =>
    val token = CSRF.getToken(rs)
    val user:Boolean = rs.session.get("user").exists( name => true)
    if(user == false ){
      Future.successful( Redirect(routes.LoginController.login()) )
    }else {
      val loginUser = rs.session.get("user").get
      ProductForm.bindFromRequest.fold(
        formWithErrors => {Logger.info("Product form error ")
          val flashData = "data error"
          Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"->token.value).flashing("createError"-> "add formWithError")  )
        },
        getProduct => {
          if(getProduct.product_name.equals(null) || getProduct.product_name.equals("")){
            Logger.info("Product form Get ")
            val flashData = "data is null"
            Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"->token.value).flashing("createError"-> "check productName")  )
          }else{
            Logger.info("create product name success")
            val flashData = "success"
            val addProduct = productDAO.createProduct(getProduct.product_name,getProduct.category_id)
            Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"->token.value).flashing("createSuccess"-> "add success ") )
          }
        }
      )
    }
  }

  /**
    * search product
    * */
  def searchProduct = Action.async { implicit  rs =>
    val token = CSRF.getToken(rs)
    val user: Boolean = rs.session.get("user").exists(name => true)
    if (user == false) {
      Future.successful(Redirect(routes.LoginController.login()))
    } else {
      val loginUser = rs.session.get("user").get
      ProductForm.bindFromRequest.fold(
        formWithErrors => {Logger.info("Product search form error " )
          Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"->token.value).flashing("searchError"-> "search formWithError")  )
        },
        SearchProduct => {
          Logger.info("search Product   ")
          val modify = productDAO.findByName(SearchProduct.product_name)
          val noData = Await.result(modify , Duration.Inf)
          if(noData.id == -1){
            Logger.info("search data not found")
            Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"->token.value).flashing("searchError"-> "not found") )
          }else{
            Logger.info("search data done")
            val listProduct:Seq[Product] = Await.result(productDAO.findAll() , Duration.Inf )
            val category = Await.result(categoryDAO.findAll(), Duration.Inf)
            modify.map( m => {  Ok(views.html.category(ProductForm,listProduct,m, category)).withSession("user"-> user.toString ,"csrfToken"->token.value)  })
          }
        }
      )
    }

  }

  /**
    * edit record
    * */
  def editProduct = Action.async{ implicit  rs =>
    val token = CSRF.getToken(rs)
    val user: Boolean = rs.session.get("user").exists(name => true)
    if (user == false) {
      Future.successful(Redirect(routes.LoginController.login()))
    } else{
      val loginUser = rs.session.get("user").get
      ProductForm.bindFromRequest.fold(
        formWithErrors => {Logger.info("Product modify form error ")
          Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"->token.value).flashing("editError"-> "modify formWithError")  )
        },
        modifyProduct => {
          Logger.info("Modify Product   ")
          val modifyDone = Await.result( productDAO.update(modifyProduct)  ,Duration.Inf)
          println("done" , modifyDone)
          Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"-> token.value).flashing("editSuccess"-> "modify done") )
        }
      )
    }
  }

  /**
    * delete product
    * */
  def delProduct = Action.async(parse.formUrlEncoded){ implicit  rs =>
    val token = CSRF.getToken(rs)
    val user: Boolean = rs.session.get("user").exists(name => true)
    if (user == false) {
      Future.successful(Redirect(routes.LoginController.login()))
    }else{
      val loginUser = rs.session.get("user").get
      val product_id = rs.body("delProduct").head.toInt
      productDAO.delete(product_id)
      Future.successful( Redirect(routes.CategoryController.CategoryIndex()).withSession("user"-> loginUser,"csrfToken"-> token.value).flashing("delSuccess"-> "del done") )
    }
  }


}
