package at.idot.scimplbetter.snippet

import at.idot.scimplbetter.model.Game
import at.idot.scimplbetter.model.Bet
import at.idot.scimplbetter.model.User
import _root_.scala.xml.{NodeSeq, Text}
import at.idot.scimplbetter.lib.PagedTableSorter

import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,S,SHtml}
import net.liftweb.common.{Box,Empty,Full}

import at.idot.scimplbetter.util.GameRepository
import net.liftweb.http.js.JsCmds.Noop
import at.idot.scimplbetter.util.Gravatar

class Users {

	def tableSorter(xhtml: NodeSeq): NodeSeq = {
		PagedTableSorter("#userstable","#thepager")
	}
	
	def showBetsFromGame(requestedGame: Box[String]): (String,Seq[(User,Option[Bet])], String, Option[Game]) = {
		requestedGame.map(_.toInt) match {
			case Full(request) => {
				GameRepository.findGameByNr(request) match {
					case Some(game) => (game.nr.toString, GameRepository.usersAndBetsFromGame(game),"", Some(game))
					case None => ("", GameRepository.allUsersWithNoneBet(),"game not found" + request, None)
				}
			}
			case _ => ("", GameRepository.allUsersWithNoneBet(),"", None)
		}
	}
	

	def allUsers(xhtml: NodeSeq): NodeSeq = {
		
		val (requestedGameNr, userBets, msg, ogame) = showBetsFromGame( S.param(Users.requestedGame))
		val loginUser = AccessControl.user
		if(msg != ""){
			S.warning(Text(msg))
		}
		if(loginUser.isDefined){
			if(!loginUser.get.canBet){ S.notice(Text("you could bet if you would register")) }
		}
			
		
		def userList(xhtml: NodeSeq): NodeSeq =  {	
			userBets.flatMap(t => {
				val user = t._1
					bind("user",xhtml,
						"userName" -> SHtml.link("/user/"+user.userName,() => {}, Text(user.userName)),
						"lastName" -> user.lastName,
						"points" -> user.points,
						"topscorer" -> (if(user.specialBet.topscorer != null) user.specialBet.topscorer.lastName else "NA"),
						"mvp" -> (if(user.specialBet.mvp != null) user.specialBet.mvp.lastName else "NA"),
						"worldchampion" -> (if(user.specialBet.winningTeam != null) user.specialBet.winningTeam.name else "NA"),
						"bets" -> {
							t._2 match {
								case Some(bet) =>{
										val atts = BetSelect.canChange(bet, loginUser)
											<td>{SHtml.ajaxSelect(BetSelect.betValues, BetSelect.selectDefault(bet.goalsTeam1), v => {bet.goalsTeam1 = BetSelect.selectedToValue(v); BetSelect.persist(bet, loginUser, "/users"); Noop}, atts: _*)} :
												{SHtml.ajaxSelect(BetSelect.betValues, BetSelect.selectDefault(bet.goalsTeam2), v => {bet.goalsTeam2 = BetSelect.selectedToValue(v); BetSelect.persist(bet, loginUser, "/users"); Noop}, atts: _*)}
											</td>
									}
								case None => NodeSeq.Empty
							}							
						},
						"betPoints" -> {
							t._2 match {
								case Some(bet) => <td>{bet.points}</td>
								case None => NodeSeq.Empty
							}
						},
						"userDetail" -> <a href={"/games/"+user.userName}>bets</a>
				)}
			)
		}
		
		
		def pageHeader(xhtml: NodeSeq): NodeSeq = {
			if(ogame.isDefined){
				val game = ogame.get
				Text("all users and bets for "+game.firstTeam.name+" - "+game.secondTeam.name+" "+game.datePrettyPrint)
			}
			else{
				Text("all users")
			}
		}
		
		
		bind("user",xhtml,
			"betstats" -> NodeSeq.Empty, // (if(requestedGameNr != "") <img src={"/betstats/"+requestedGameNr}/> else NodeSeq.Empty ),
			"pageHeader" -> pageHeader _,
			"pointsHeader" -> (if(requestedGameNr != "") <th>{"points "+requestedGameNr}</th> else NodeSeq.Empty ),
			"betsHeader" -> (if(requestedGameNr != "") <th>{"bets for Nr."+requestedGameNr}</th> else NodeSeq.Empty ),
			"list" -> userList _
			
		)
	}

	
}

object Users {
    val requestedGame = "requestedGame"
}
