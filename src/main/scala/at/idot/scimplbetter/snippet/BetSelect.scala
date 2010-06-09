package at.idot.scimplbetter.snippet

import at.idot.scimplbetter.model.Team
import at.idot.scimplbetter.model.Player
import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.model.Bet
import net.liftweb.common.{Box,Empty,Full}
import net.liftweb.http.{RequestVar,S,SHtml}
import at.idot.scimplbetter.util.GameRepository
import java.util.regex.Pattern

/***
 * Helpers for snippets:
 * SHtml select helpers/converters styles
 * 
 * some validation functions
 * 
 *
 */
object BetSelect {
	
	/**
	 * map of teams -> players
	 * cheap caching
	 */
	lazy val teams2players = teams2playersMap()
	lazy val teams2playersString = map2String(teams2players)
	
	
	def players2select(players: Seq[Player]) = players.map(p => (p.id.toString,playerSelect(p))).toList
		
	def map2String(teamPlayers: Map[Team,Seq[Player]]): Map[String,Seq[String]] = {
		val strs = for((team,players) <- teamPlayers) yield (team.name, players.map(p => playerSelect(p)))
		strs.toMap
	}
	

	
	/**
	 * player to select string
	 */
	def playerSelect(player: Player) = player.lastName+", "+player.firstName
	
	val NoBetString = "-"
		
	val betValues: Seq[(String,String)] = (NoBetString :: (0 to 12).toList) map (i => (i.toString, i.toString))
	
	def selectDefault(goals: Option[Int]): Box[String] = {
		goals match {
			case Some(int) => Full(int.toString)
			case None => Full(NoBetString)
		}
	}
	
	def selectedToValue(string: String): Option[Int] = {
		string match {
			case NoBetString => None
			case _ => Some(Integer.parseInt(string))
		}
	}
	
	val noChangeAtts = Array(("readonly","readonly"),("disabled","disabled"), ("class","cantBetSelect"))
	val changeAtts: Array[(String,String)] = Array()
	
	def selAtts(open: Boolean) = if(open) changeAtts else noChangeAtts
	
	
	def canChange(bet: Bet, user: Option[User]): Array[(String,String)] = {
		if(bet.canChange(user)){
			changeAtts
		}
		else{
			noChangeAtts
		}
	}
	
	def playerToString(player: Player): String = player.lastName + "," + player.firstName + " " + player.team.name

	def playersObjSeq() :Seq[(Player, String)] = {
		val players = GameRepository.allPlayers()
		mapPlayers(players)
	}	
	
	def mapPlayers(players: Seq[Player]) = players.map(p => (p, playerToString(p)))

	
	def teamObjSeq: Seq[(Team,String)] = {
		val teams = GameRepository.allTeams()
		val teamStrings = teams.map(t => (t, t.name))
		teamStrings
	}
	
	def teams2playersMap(): Map[Team, Seq[Player]] = {
		import scala.collection.JavaConversions._
		 val teams = GameRepository.allTeamsWithPlayers()
		 val teamPlayers = for(team <- teams) yield {
			 val players = team.players.toList.sortBy(_.lastName)
			 (team,players)
		 }
		 teamPlayers.toMap
	}
	
	
	/***
	* Working around things I perceive as ajax/lift/jpa problems.
	* using the loaded entities as DTO that copy their changed
	* state into the db after ajax update
	* Otherwise I think I would get staleobjectsexceptions or the
	* view would be refreshed or the lift created closures for
	* ajax would not be attached to the entitymanager merged 
	* entities.
	* 
	*/
	def persist(viewBet: Bet, user: Option[User], errorRedirect: String): Unit = {
		if(!viewBet.canChange(user)){
			S.error("Can not update bet, game closed rien ne va plus: " + viewBet.game.nr)
			S.redirectTo(errorRedirect)
			return
		}
		val em = GameRepository.newEM
		val dbBet = em.find(classOf[Bet], viewBet.id).get
		dbBet.goalsTeam1 = viewBet.goalsTeam1
		dbBet.goalsTeam2 = viewBet.goalsTeam2
		em.mergeAndFlush(dbBet)
		em.close
	}

	val button = Array(("onmouseover","this.className='bb bbhov'"),("onmouseout","this.className='bb'"), ("class","bb"))
	
	
	//from Mapper
	val defaultPattern = Pattern.compile("^[a-z0-9._%\\-+]+@(?:[a-z0-9\\-]+\\.)+[a-z]{2,4}$")
	val emailPatternUpperCase = Pattern.compile("^[a-zA-Z0-9._%\\-+]+@(?:[a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,4}$")
	
	val emailPattern = emailPatternUpperCase
	
	def validEmail(email: String): Boolean = emailPattern.matcher(email).matches

}