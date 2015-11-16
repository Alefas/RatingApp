package dao

import javax.inject.{Inject, Singleton}

import models.{Game, User}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * @author Alefas
  * @since  14/11/15
  */
@Singleton
class GamesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UsersComponent
  with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  class Games(tag: Tag) extends Table[Game](tag, "GAME") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def user1 = column[Long]("USER1")
    def user2 = column[Long]("USER2")
    def result = column[Int]("RESULT")

    def * = (id.?, user1, user2, result) <> (Game.tupled, Game.unapply)
  }

  val games = TableQuery[Games]

  /** Insert a new user */
  def insert(game: Game): Future[Unit] =
    db.run(games += game).map(_ => ())
}
