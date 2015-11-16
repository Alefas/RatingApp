package models

/**
  * @author Alefas
  * @since  14/11/15
  */
case class User(id: Option[Long], facebookId: String, rating: Int = 1200)
case class Game(id: Option[Long], user1id: Long, user2id: Long, result: Int)
