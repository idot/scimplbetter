package at.idot.scimplbetter.snippet

import at.idot.scimplbetter.model.Game
import at.idot.scimplbetter.model.Bet
import at.idot.scimplbetter.model.User
import _root_.scala.xml.{NodeSeq, Text}

import scala.collection.mutable.ListBuffer
import scala.collection.immutable.TreeMap

import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,S,SHtml}
import net.liftweb.common.{Box,Empty,Full}

import at.idot.scimplbetter.util.GameRepository
import net.liftweb.http.js.JsCmds.Noop
import at.idot.scimplbetter.util.Gravatar



class Winners {
	
	def announce(xhtml: NodeSeq): NodeSeq = {
		
		
		def userList(xhtml: NodeSeq): NodeSeq =  {	
			val users = GameRepository.allUsersNonAdmin()
			val winnerRanks = Winners.selectWinners(users)
			winnerRanks.flatMap(t => {
				val user = t._1
					bind("user",xhtml,
						"gravatar" -> Gravatar(user.gravatarEmail, 50, "r", user.gravatarDefault ),
						"rank" -> t._2 .toString,
						"userName" -> SHtml.link("/user/"+user.userName,() => {}, Text(user.userName)),
						"firstName" -> user.firstName,
						"lastName" -> user.lastName,
						"points" -> user.points,
						"bets" -> <a href={"/games/"+user.userName}>bets</a>
				)}
			)
		}
		
		
		bind("winners",xhtml,
				"title" -> (if(Winners.announced) "Winners" else "Leaders"),
				"congrats" -> (if(Winners.announced) "We congratulate the winners and hope everybody had a good time." else ""),
				"list" -> userList _
		)
	}
	
	
}


/****
 * THIS IS NOT TESTED!!!!!!!
 * 
 * if winners are announced this global object hodst this information
 * Its not persistent on purpose so a simple restart erases the information
 * in case it was announced erroneously
 *
 */
object Winners {
	var announced = false
	
	/***
	 * Don't know how to select winners if equal points.
	 * Ad hoc algorithm is:
	 * sort users by points.
	 * go thrgouh list either min 3 user or max 3. rank.
	 *  
	 * 
	 * 
	 *
	 */
	def selectWinners(users: Seq[User]): Seq[(User,Int)] = {
		val sortedUsers = users.sortBy(_.points).reverse
		val points = sortedUsers.map(_.points)
		val rankList = rankByPoints(points)
		var winners = winnerRanks(rankList)
		sortedUsers.take(winners.size).zip(winners)
	}
	//TODO Unit tests
	def winnerRanks(rankList: Seq[Int]) = {
		val winners = rankList.zipWithIndex.takeWhile { (t) =>
			val index = t._2
			val rank = t._1 
			if(rank > 3){
				false
			}
			else {
				if(index > 3){
					false
				}
				else{
					true
				}
			}
		}
		winners.map(_._1)
	}
	
	def rankByPoints(points: Seq[Int]) = {//could be made fold or breakable
		var rank = 0
		var prevPoint = 0
		val rankList = ListBuffer[Int]()
		for(point <- points){
			if(prevPoint != point){
				prevPoint = point
				rank += 1
			}
			rankList += rank
		}
		rankList
	}
	
}