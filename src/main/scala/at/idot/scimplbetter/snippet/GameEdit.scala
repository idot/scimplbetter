package at.idot.scimplbetter.snippet

import _root_.scala.xml.{NodeSeq, Text}
import net.liftweb.widgets.tablesorter.TableSorter

import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,S,SHtml}
import net.liftweb.common.{Box,Empty,Full}

import at.idot.scimplbetter.model._
import at.idot.scimplbetter.util.GameRepository


/**
* Could also be used for new games. The game Nr could be filled up automatically highestGameNr + 1 and an ajaxsuggest ?
*
*/
class GameEdit {

	
	
	def edit(xhtml: NodeSeq): NodeSeq = {
		
	
		var tmpGame: Game = null
		
		def wrongGame(xhtml: NodeSeq, parameter: String ): NodeSeq = {
			bind("game", chooseTemplate("choose", "notExist", xhtml),
			  "param" -> Text("Could not find game with this number " +  parameter))
		}
		
		def submit() {
			if(!tmpGame.closed){
				S.error("game not closed yet")
				S.redirectTo("/games")
			}
			val em = GameRepository.newEM
			tmpGame.result.setted = true
			em.mergeAndFlush(tmpGame)
			em.close()
			GameRepository.calculatePoints()			
			S.redirectTo("/game/"+tmpGame.nr)
		}
		
		def cancel(){
			S.redirectTo("/games")
		}
		
		def delete(){
			S.redirectTo("/games")
		}
		
		
		//TODO: DATE!!!
		def editGame(xhtml: NodeSeq): NodeSeq = {
			val goals = (0 to 13).map(g => (g, g.toString))
		//	val levels = GameRepository.allLevels.map(l => (l.levelNr.toString, l.level))
		//	val teams = GameRepository.allTeams.map(t => (t, t.name ))
			bind("game", chooseTemplate("choose", "validGame", xhtml),
		//	   "gameNr" -> SHtml.text(tmpGame.nr.toString, i => tmpGame.nr = Integer.parseInt(i) ),
		//	   "firstTeam" -> SHtml.selectObj(teams, Full(tmpGame.firstTeam.id.toString), n => tmpGame.firstTeam = GameRepository.find(classOf[Team], n.toInt).get),
		//	   "secondTeam" -> SHtml.select(teams, Full(tmpGame.secondTeam.id.toString), n => tmpGame.secondTeam = GameRepository.find(classOf[Team],n.toInt).get),  
		//	   "level" ->  SHtml.select(levels, Full(tmpGame.level.levelNr.toString), l => tmpGame.level = GameRepository.levelNrToLevel(l.toInt)),
			   "gameNr" -> Text(tmpGame.nr.toString),
			   "name" -> Text(tmpGame.firstTeam.name+" - "+tmpGame.secondTeam.name),
			   "firstGoals" -> (if(tmpGame.result.setted || !tmpGame.closed) Text(tmpGame.result.goalsTeam1.toString) else SHtml.selectObj(goals, Empty, (g:Int) => tmpGame.result.goalsTeam1 = g)),
			   "secondGoals" -> (if(tmpGame.result.setted || !tmpGame.closed) Text(tmpGame.result.goalsTeam2.toString) else SHtml.selectObj(goals, Empty, (g:Int) => tmpGame.result.goalsTeam2 = g)),
			   "submit" -> SHtml.submit("submit", submit ), 
			   "cancel" -> SHtml.submit("cancel", cancel ),
			   "delete" -> SHtml.submit("not functional", delete) //add javascript question
			 )
		}
		
		def betForGame(xhtml: NodeSeq): NodeSeq = {
			NodeSeq.Empty 
		}

		def showGame(xhtml: NodeSeq): NodeSeq = {
			NodeSeq.Empty 
		}
		
		
		S.param("gameNr").map(_.toInt) match {
			case Full(b) => {
			    GameRepository.findGameByNr(b) match {
			    	case Some(game) => {
			    		tmpGame = game
			    		AccessControl.user match {
			    			case Some(user) if user.isAdmin => editGame(xhtml)
			//    			case Some(user) if user.canBet => betForGame(xhtml)
			    			case _ => showGame(xhtml)
			    		}
			    	}
			    	case _ => wrongGame(xhtml, b.toString) //S.param new Game && user isAdmin tmpGame = new Game...
			    }
			}
			case _ => wrongGame(xhtml, "no or wrong parameter given")
		}
		
		
		
	}
	
	
	
}