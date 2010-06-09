package at.idot.scimplbetter.snippet

import at.idot.scimplbetter.model.Team
import at.idot.scimplbetter.model.Player
import at.idot.scimplbetter.model.Bet
import at.idot.scimplbetter.model.Game
import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.util.Gravatar
import _root_.scala.xml.{NodeSeq, Text}
import at.idot.scimplbetter.lib.PagedTableSorter
import net.liftweb.http.js.JsCmds.Noop

import scala.xml._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,S,SHtml}
import net.liftweb.common.{Box,Empty,Full}
import net.liftweb.common.Logger
import at.idot.scimplbetter.util.GameRepository
import net.liftweb.http.js._
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JE
import net.liftweb.http.js.jquery.JqJE
import net.liftweb.http.js.JsCmds._

class UserDetails {
	
	//this would all be much easier if there was an untrustedObjSelect
	//now I have to convert to/from strings
	def details(xhtml: NodeSeq): NodeSeq = {

		val orequestedUser = S.param("requestedUser") match {
			case Full(userName) => GameRepository.findUserByUserName(userName) 
			case _ => {
				S.error(" Wrong request ??")
				S.redirectTo("/games")
			}
		}
		if(!orequestedUser.isDefined){
			S.error(" could not find user " + S.param("requestedUser").get)
			S.redirectTo("/games")
		}
		
		val requestedUser = orequestedUser.get
		
3		
		if(AccessControl.loggedInUserName.isDefined && AccessControl.loggedInUserName.get == requestedUser.userName){
			val (nametop, jstop) = SHtml.ajaxCall(JE.JsRaw("this.value"), s => After(200,  replacePlayers(s,"top_select")))
			val (namemvp, jsmvp) = SHtml.ajaxCall(JE.JsRaw("this.value"), s => After(200,  replacePlayers(s,"mvp_select")))
			
			val allPlayers = BetSelect.playersObjSeq()  
			val allTeams = BetSelect.teamObjSeq
			var mvpTeam = if(requestedUser.specialBet.mvp != null) requestedUser.specialBet.mvp.team.name else allPlayers.head._1.team.name
			var mvp = if(requestedUser.specialBet.mvp != null) BetSelect.playerSelect(requestedUser.specialBet.mvp) else BetSelect.playerSelect(allPlayers.head._1)
			var topTeam = if(requestedUser.specialBet.topscorer != null) requestedUser.specialBet.topscorer.team.name else allPlayers.head._1.team.name
			var top = if(requestedUser.specialBet.topscorer != null) BetSelect.playerSelect(requestedUser.specialBet.topscorer) else BetSelect.playerSelect(allPlayers.head._1)
	
			val winningTeam = null
			val semifinal1 = null
			val semifinal2 = null
			val semifinal3 = null
			val semifinal4 = null
			
			val open = !GameRepository.specialBetsClosed()
			
			def doChange(): Unit = {
				if(!BetSelect.validEmail(requestedUser.email)){
					S.error("email","email not valid")
				}
				if(S.errors.size == 0 && !requestedUser.isAdmin){
					val em = GameRepository.newEM
					val append = if( !requestedUser.hadInstructions || !requestedUser.canBet ) ". Now you can bet." else "."
					requestedUser.hadInstructions = true
					requestedUser.canBet = true
					
					val merged = em.merge(requestedUser)
					if(!open && merged.specialBet.different(requestedUser.specialBet)){
						S.error("Sorry time is up. No more special bet changes.")
						S.redirectTo("/myHome")
					}
					if(open){
						val themvp = getPlayer(mvpTeam, mvp)
						if(themvp.isDefined){
							merged.specialBet.mvp = em.merge(themvp.get)
						}
						val thetop = getPlayer(topTeam, top)
						if(thetop.isDefined){
							merged.specialBet.topscorer = em.merge(thetop.get)
						}		
					}
					em.persistAndFlush(merged)
					em.close()
					S.notice("detailsNotice",Text("successfully updated "+requestedUser.userName+append))
					S.redirectTo("/myHome")
				}
			}
			
			def getPlayer(teamName: String, playerName: String): Option[Player] = {
				allTeams.find(_._2 == teamName) match {
					case Some(t) => {
						val li = BetSelect.teams2players.keys.toList.sortBy(_.name)
						val sec = li(1)
						val playerSeq = BetSelect.teams2players.get(t._1)
						playerSeq.get.find(BetSelect.playerSelect(_) == playerName)
					}
					case _ => None
				}
			}
			
			
		    bind("details",xhtml,
			"username" -> requestedUser.userName,
			"gravatar" -> Gravatar(requestedUser.gravatarEmail, 50, "r", requestedUser.gravatarDefault ),
			"points" -> requestedUser.points,
			"firstname" -> SHtml.text(requestedUser.firstName,s => requestedUser.firstName = s, ("size","20")),
			"lastname" -> SHtml.text(requestedUser.lastName,s => requestedUser.lastName = s, ("size","20")),
			"email" -> SHtml.text(requestedUser.email,s => requestedUser.email = s, ("size","30")),
			"gravataremail" -> SHtml.text(requestedUser.gravatarEmail,s => requestedUser.gravatarEmail = s, ("size","30")),
			"gravatardisplay" -> SHtml.select(User.gravatarDefault.map(d => (d,d)), Full(requestedUser.gravatarDefault),s => requestedUser.gravatarDefault = s),
			"institute" -> SHtml.text(requestedUser.institute,s => requestedUser.institute = s, ("size","20")),
			"betsLink" -> betLink(requestedUser.userName),
			"sendExcel" -> SHtml.checkbox(requestedUser.emailCheck, s => requestedUser.emailCheck = s ),
			"topscorerTeam" -> (if(open) SHtml.select(allTeams.map(s =>( s._1.name,s._1.name)), Full(topTeam), s => topTeam = s, "onchange" -> jstop.toJsCmd) % (new PrefixedAttribute("lift", "gc", nametop, Null)) else NodeSeq.Empty),
			"topscorerPlayer" ->  playerChoice(topTeam, s => top = s, top, open) % ("id" -> "top_select"),
			"mvpTeam" -> (if(open) SHtml.select(allTeams.map(s =>( s._1.name,s._1.name)), Full(mvpTeam), s => mvpTeam = s, "onchange" -> jsmvp.toJsCmd) % (new PrefixedAttribute("lift", "gc", namemvp, Null)) else NodeSeq.Empty),
			"mvpPlayer" ->  playerChoice(mvpTeam, s => mvp = s, mvp, open) % ("id" -> "mvp_select"),
			"bestTeam" ->  selectItem(requestedUser.specialBet.winningTeam, allTeams, (t:Team) => t.name , requestedUser.specialBet.winningTeam_= _ ,open),		
			"semifinal1" ->  selectItem(requestedUser.specialBet.semifinal1, allTeams, (t:Team) => t.name , requestedUser.specialBet.semifinal1_= _ ,open),		
			"semifinal2" ->  selectItem(requestedUser.specialBet.semifinal2, allTeams, (t:Team) => t.name , requestedUser.specialBet.semifinal2_= _ ,open),		
			"semifinal3" ->  selectItem(requestedUser.specialBet.semifinal3, allTeams, (t:Team) => t.name , requestedUser.specialBet.semifinal3_= _ ,open),		
			"semifinal4" ->  selectItem(requestedUser.specialBet.semifinal4, allTeams, (t:Team) => t.name , requestedUser.specialBet.semifinal4_= _ ,open),		
			"submit" -> SHtml.submit("submit", doChange _ , BetSelect.button: _* ),
			"cancel" -> SHtml.submit("cancel", () => S.redirectTo("/games") , BetSelect.button: _* ),
			"changePassword" -> SHtml.submit("change password", () => S.redirectTo("/authentication/changePassword") , BetSelect.button: _* )
			)
		}
		else{
			bind("details",xhtml,
			"username" -> requestedUser.userName,
			"gravatar" -> Gravatar(requestedUser.gravatarEmail, 42, "r", requestedUser.gravatarDefault ),
			"points" -> requestedUser.points,
			"firstname" -> requestedUser.firstName,
			"lastname" -> requestedUser.lastName,
			"email" -> "visible only for user",
			"gravataremail" -> "visible only for user",
			"gravatardisplay" -> "visible only for user",
			"institute" -> requestedUser.institute,
			"betsLink" -> betLink(requestedUser.userName),
			"sendExcel" -> SHtml.checkbox(requestedUser.emailCheck, s => {}, BetSelect.noChangeAtts: _* ),
			"topscorerTeam" -> NodeSeq.Empty,
			"topscorerPlayer" ->  showPlayer(requestedUser.specialBet.topscorer),
			"mvpTeam" -> NodeSeq.Empty,
			"mvpPlayer" -> showPlayer(requestedUser.specialBet.mvp),
			"bestTeam" ->  showTeam(requestedUser.specialBet.winningTeam),
			"semifinal1" -> showTeam(requestedUser.specialBet.semifinal1),
			"semifinal2" -> showTeam(requestedUser.specialBet.semifinal2),
			"semifinal3" -> showTeam(requestedUser.specialBet.semifinal3),
			"semifinal4" -> showTeam(requestedUser.specialBet.semifinal4),
			"submit" -> NodeSeq.Empty,
			"cancel" -> NodeSeq.Empty,
			"changePassword" -> NodeSeq.Empty
			)
		}
	}
	
	
	//narrows down players upon team select
    //like AjaxForm example
	//seems like I have to use strings
	def playerChoice(team: String, f:(String) => Unit, playerName: String, open: Boolean): Elem = {
		val players = BetSelect.teams2playersString.get(team).getOrElse(List())
	    SHtml.untrustedSelect(players.map(p => (p,p)), Full(playerName), s=> f(s), BetSelect.selAtts(open): _* )
	}
	
	def replacePlayers(team: String, selectName: String): JsCmd = {
		val players = BetSelect.teams2playersString.get(team).getOrElse(List())
		val first = players.head
		ReplaceOptions(selectName,players.map(p => (p,p)).toList, Full(first))
	}
	
	
	def betLink(userName: String) = SHtml.link("/games/"+userName, () => {}, Text(userName+"'s bets"))

	def showTeam(team: Team) = if(team == null) "NA" else team.name
	def showPlayer(player: Player) = if(player == null) "NA" else BetSelect.playerToString(player)
	
	def selectItem(t: Team, allT: Seq[(Team,String)], f:(Team)=> String, bet: (Team) => Unit, open: Boolean): NodeSeq = {
		if(open){
			val filtered = filterItems(t, allT, f)
			SHtml.selectObj(filtered, (if(t == null) Empty else Full(t)), (t:Team)  => bet(t))
		}
		else{
			Text(showTeam(t))
		}
	}
		
	//selectObj works with obj identity so we have to filter and append our hibernate retrieved entities
	def filterItems[T](t: T, allT: Seq[(T,String)], f:(T) => String): Seq[(T,String)] = {
		if(t != null){
			(t, f(t)) :: allT.filter(t != _._1).toList
		}
		else{
			allT
		}
	}
	
}


