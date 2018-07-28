package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class UsersDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider ) extends HasDatabaseConfigProvider[JdbcProfile]{


  import dbConfig.profile.api._

  private val users = TableQuery[usersTable]

  def findAll() : Future[Seq[Users]]  = db.run(users.result)

  def findById(id: Int) = db.run(
    users.filter(_.id === id).result.headOption
  )

  def findByName(name:String) ={
    val f = db.run(users.filter(_.user === name).result.headOption)
    f.map{
      result => result match {
        case Some(u) => u
        case None => throw new Exception("User not Found")
      }
    }
  }

  def checkUserOrPassword(user:String,password:String):Boolean = {
//    println("input data= "+user,password)
    val checkUser = db.run(users.filter(x=> x.user===user && x.password=== password).exists.result)
    Await.result(checkUser, Duration("5 seconds"))
  }

  def install(newUser: Users) = {
    db.run( ( users returning users.map(_.id) ) += newUser)
  }

  def update(user :Users):Future[Int] = {
    db.run( users.filter(_.id === user.id )
    .map(a=> (a.user,a.password,a.role))
    .update(user.user,user.password,user.role)
    )
  }


   class usersTable(tag: Tag) extends Table[Users](tag, "user") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def user = column[String]("user")

    def password = column[String]("password")

    def role = column[String]("role")

    def * = (id, user, password, role) <>( (Users.apply _).tupled, Users.unapply)
  }

}
