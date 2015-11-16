package dao

import javax.inject.{Inject, Singleton}

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @author Alefas
  * @since  14/11/15
  */
trait UsersComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Users(tag: Tag) extends Table[User](tag, "USER") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def facebookId = column[String]("FACEBOOK_ID")
    def rating = column[Int]("RATING")
    def * = (id.?, facebookId, rating) <> (User.tupled, User.unapply)
  }
}

@Singleton
class UsersDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UsersComponent
  with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val users = TableQuery[Users]

  /** Insert a new user */
  def insert(user: User): Future[Unit] =
    db.run(users += user).map(_ => ())

  def userByFacbookId(facebookId: String): Future[Option[User]] = {
    db.run(users.filter(_.facebookId === facebookId).result.headOption)
  }

  def getOrCreate(facebookId: String): Future[User] = {
    userByFacbookId(facebookId).flatMap {
      case Some(user) => Future(user)
      case None =>
        val user = User(None, facebookId)
        db.run(users += user).flatMap { r =>
          userByFacbookId(facebookId).map(_.get)
        }
    }
  }

  def updateRating(id: Long, rating: Int): Future[Unit] = {
    db.run(users.filter(_.id === id).map(_.rating).update(rating)).map(_ => ())
  }
}
