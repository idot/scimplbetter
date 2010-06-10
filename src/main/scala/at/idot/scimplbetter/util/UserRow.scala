package at.idot.scimplbetter.util

import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.model.Bet

case class Row(size: Int){
	
	val values = new Array[String](size)
	
	def set(value: String, index: Int){
		values(index) = value
	}
	
	def get(index: Int) = values(index)
	
}

class UserRowCreator{
	
	def createUserRows(): Seq[UserRow] = {
		val userRows = for(user <- GameRepository.allUsersNonAdmin) yield {
			val bets = GameRepository.betsForUser(user)
			UserRow(user, bets)
		}
		userRows.toList
	}

	def assignLeading(userRows: Seq[UserRow]){
		val pointsorted = userRows.sortBy(_.user.points).reverse
		val top = pointsorted.headOption
		if(top.isDefined && top.get.user.points > 0){
			val topScore = top.get.user.points
			for(ur <- pointsorted.takeWhile(_.user.points == topScore)){
				ur.leader = true
			}
		}
	}
	
}

trait Leading {
	var leader = false
}

//should be user + cumulative points for evaluated games
//TODO
//chart at statistics/points
case class UserPointsToNow(user: User, cumulatedPoints: Row) extends Leading {
	
}

object UserPointsToNow {
	
	
}

case class UserRow(user: User, games: Row, pointsPerGame: Row, cumulatedPoints: Row, firstGoals: Row, secondGoals: Row, resultFirstTeam: Row, resultSecondTeam: Row ) extends Leading {
	
}

object UserRow {
	val noBetString = "-"
	

	def apply(user: User, bets: Seq[Bet]): UserRow = {//bets have to be sorted by gameNr
		val size = bets.size
		val pointsPerGame = new Row(size)
		val cumulatedPoints = new Row(size)
		val firstGoals = new Row(size)
		val secondGoals = new Row(size)
		val resultFirstTeam = new Row(size)
		val resultSecondTeam = new Row(size)
		val games = new Row(size)
		var cPoints = 0
		for(bet <- bets){
			val nr = bet.game.nr - 1 //nr is 1 based
			if(bet.calculated){
				var points = bet.points
				cPoints += points
				pointsPerGame.set(points.toString, nr)
				resultFirstTeam.set(bet.game.result.goalsTeam1.toString, nr)
				resultSecondTeam.set(bet.game.result.goalsTeam2.toString, nr)
			}	
			else{
				pointsPerGame.set(noBetString, nr)
				resultFirstTeam.set(noBetString, nr)
				resultSecondTeam.set(noBetString, nr)
			}
			games.set(bet.betDisplayPretty, nr)
			cumulatedPoints.set(cPoints.toString, nr)	
			firstGoals.set(b2s(bet.goalsTeam1), nr)
			secondGoals.set(b2s(bet.goalsTeam2), nr)
		}
		new UserRow(user, games, pointsPerGame, cumulatedPoints, firstGoals, secondGoals, resultFirstTeam, resultSecondTeam)
	}
	
	/***
	 * bet to string
	 */
	def b2s(bet: Option[Int]): String = {
		bet match {
			case Some(b) => b.toString
			case None => noBetString
		}
	}
}
	
	
	
