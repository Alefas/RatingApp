package controllers

import javax.inject.Inject

import dao.{GamesDAO, UsersDAO}
import models.Game
import org.pac4j.core.profile.CommonProfile
import org.pac4j.oauth.profile.facebook.FacebookProfile
import org.pac4j.play.scala.Security
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject ()(usersDAO: UsersDAO, gamesDAO: GamesDAO) extends Security[CommonProfile] {

  def index = Action { request =>
    val newSession = getOrCreateSessionId(request)
    Ok(views.html.index()).withSession(newSession)
  }

  def facebookIndex = RequiresAuthentication("FacebookClient") { profile =>
    Action { request =>
      val facebookProfile = getUserProfile(request).asInstanceOf[FacebookProfile]
      Ok(views.html.protectedIndex(facebookProfile))
    }
  }

  def rating = Action.async { implicit request =>
    val facebookId = request.queryString.get("facebookId").get.head
    usersDAO.getOrCreate(facebookId).map(user => Ok(JsObject(Seq("rating" -> JsNumber(user.rating)))))
  }

  def addGame(opponent: String, result: Int) = Action.async { implicit request =>
    def newRating(rating1: Int, rating2: Int, result: Int): Int = {
      val E = 1.0 / (1.0 + math.pow(10, (rating2 - rating1) / 400.0) )
      val S = result match {
        case -1 => 0.0
        case 0 => 0.5
        case 1 => 1.0
      }
      (rating1 + 30 * (S - E)).round.toInt
    }
    val facebookProfile = getUserProfile(request).asInstanceOf[FacebookProfile]
    val id = facebookProfile.getId
    for {
      user1 <- usersDAO.getOrCreate(id)
      user2 <- usersDAO.getOrCreate(opponent)
      rating1 = user1.rating
      rating2 = user2.rating
      newRating1 = newRating(rating1, rating2, result)
      newRating2 = newRating(rating2, rating2, -result)
      _ <- usersDAO.updateRating(user1.id.get, newRating1)
      _ <- usersDAO.updateRating(user2.id.get, newRating2)
      game = Game(None, user1.id.get, user2.id.get, result)
      _ <- gamesDAO.insert(game)
    } yield Ok(views.html.protectedIndex(facebookProfile))
  }
}
