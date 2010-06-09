package at.idot.scimplbetter.snippet

import at.idot.scimplbetter.model.Bet
import at.idot.scimplbetter.model.Game
import at.idot.scimplbetter.model.User
import _root_.scala.xml.{NodeSeq, Text}
import at.idot.scimplbetter.lib.PagedTableSorter
import net.liftweb.http.js.JsCmds.Noop

import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,S,SHtml}
import net.liftweb.common.{Box,Empty,Full}
import net.liftweb.common.Logger
import at.idot.scimplbetter.util.GameRepository
import at.idot.scimplbetter.util.Gravatar

//TODO: add points to bets from user for closed games
//
class Games extends Logger {
	
	def tableSorter(xhtml: NodeSeq): NodeSeq = {
		PagedTableSorter("#gamestable","#thepager")
	}
		
	def showGamesBetsFromUser(requestedUser: Box[String]): (String, Seq[(Game,Option[Bet])], String, Option[User]) = {
		 requestedUser match {
			case Full(request) => {
				GameRepository.findUserByUserName(request) match {
					case Some(user) if !user.isAdmin => (user.userName, GameRepository.gamesAndBetsFromUser(user),"", Some(user))
					case Some(user) if user.isAdmin => ("", GameRepository.allGamesWithNoneBet, " "+request+" is admin user. no bets", Some(user))
					case _ => ("", GameRepository.allGamesWithNoneBet," "+request+" not found as user", None)
				}				
			}
			case _ => ("",GameRepository.allGamesWithNoneBet,"",None)
		}
	}
	

	
	def allGames(xhtml: NodeSeq): NodeSeq = {
		
		val (requestedUser,gamesBets, msg, requestedUserOpt) = showGamesBetsFromUser( S.param(Games.requestedUser) ) //TODO: show error message if user not found
		val user = AccessControl.user
		if(msg != ""){
			S.warning(Text(msg))
		}
		val admin = user match {
			case Some(u) => {
				if(!u.canBet){ S.notice(Text("To bet you have to submit your details!")) }
				u.isAdmin 
			}
			case _ => false
		}
		
		def gameList(xhtml: NodeSeq): NodeSeq =  {	
			
			gamesBets.flatMap(t => {
					val game = t._1
					bind("game",xhtml,
						"nr" -> game.nr,
						"team1" -> game.firstTeam.name,
						"team2" -> game.secondTeam.name,
						"date" -> game.datePrettyPrint,
						"result" -> game.resultPrettyPrint,
						"level" -> game.level.level, //could be made to a link or popup for points
						"points" -> {
							t._2 match {
								case Some(bet) => <td>{if(bet.calculated) bet.points else "NA"}</td>
								case _ => NodeSeq.Empty
							}
						},
						"bets" -> {
							t._2 match {
								case Some(bet) => {
										val atts = BetSelect.canChange(bet, user)
											<td>{SHtml.ajaxSelect(BetSelect.betValues, BetSelect.selectDefault(bet.goalsTeam1), v => {bet.goalsTeam1 = BetSelect.selectedToValue(v); BetSelect.persist(bet, user, "/games"); Noop}, atts: _*)} :
												{SHtml.ajaxSelect(BetSelect.betValues, BetSelect.selectDefault(bet.goalsTeam2), v => {bet.goalsTeam2 = BetSelect.selectedToValue(v); BetSelect.persist(bet, user, "/games"); Noop}, atts: _*)}
											</td>
									}
								case None => NodeSeq.Empty
							}
						},
						"gamesDetail" -> <a href={"/users/"+game.nr}>detail</a>,
						"setResult" -> (if(admin) <td><a href={"/game/"+game.nr}>set</a></td> else NodeSeq.Empty )
					)}
				)
		}
		
		def pageHeader(xhtml: NodeSeq): NodeSeq = {
			if(requestedUser != ""){
				Text("all games and "+userNamesHeader(requestedUser, user))
			}
			else{
				Text("all games")
			}
		}
	
		
		bind("game",xhtml,
			"adminHeader" -> (if(admin) <th>result</th> else NodeSeq.Empty ),
			"pageHeader" -> pageHeader _,
			"pointsHeader" -> (if(requestedUser != "") <th>points</th> else NodeSeq.Empty),
			"gravatar" -> { requestedUserOpt match {
				case Some(rq) => Gravatar(rq.gravatarEmail, 50, "r", rq.gravatarDefault )
				case _ => NodeSeq.Empty
			}},
   			"betsHeader" -> (if(requestedUser != "") <th>{userNamesHeader(requestedUser, user)}</th> else NodeSeq.Empty),
   			"list" -> gameList _ 
   		)
	}

	def userNamesHeader(requestedUser: String, loggedInUser: Option[User]): String = {
		loggedInUser match {
			case Some(user) => {
				if(user.userName == requestedUser){
					"your bets"
				}
				else{
					requestedUser+"'s bets"
				}
			}
			case _ => requestedUser+"'s bets"
		}
	}
	
}



object Games {
	val requestedUser = "requestedUser"
}
	


