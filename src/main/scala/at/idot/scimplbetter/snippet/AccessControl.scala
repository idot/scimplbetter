package at.idot.scimplbetter.snippet

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.common._
import net.liftweb.common.{Box,Empty,Full}
import net.liftweb.http.SessionVar
import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.util.GameRepository
import net.liftweb.http.{S, LiftSession}
import at.idot.scimplbetter.comet.Chat

object CurrentUserName extends SessionVar[Box[String]](Empty) 
object RequestedURL extends SessionVar[Box[String]](Empty) 
//object CurrentUser extends RequestVar[Box[User]](Empty) //not used

object AccessControl {

	def logout(): Box[LiftResponse] = { 
//		CurrentUser(Empty) 
		CurrentUserName(Empty) 
		changeChat("")
		Full(RedirectResponse(S.param("path").openOr("/"))) 
	}

	def login(user: User) = {
//		CurrentUser(Full(user)) 
    	CurrentUserName(Full(user.userName))
    	changeChat(user.userName)
	}
	
	def changeChat(userName: String){
		for( s <- S.session;
			comet <- s.findComet("Chat")	
		){
			comet match {
				case c: Chat => {
					c.setUser(userName)
				}
			}
		}
	}
	

	
	def seessionExpires(s: LiftSession){
		//TODO: check if this is necessary or prolongs session!! CurrentUserName(Empty)
		changeChat("")
	}
	
	def user: Option[User] = {
		CurrentUserName.is match {
			case Full(userName) => GameRepository.findUserByUserName(userName)
			case _ => None
		}
	}
	
	def loggedIn(): Boolean = {
		CurrentUserName.is match {
			case Full(name) => true
			case _ => false
		}
	}
	
	//TODO: check if SessionVar user not cheaper
	//this makes lookup on every page access!
	def isAdmin(): Boolean = {
		CurrentUserName.is match {
			case Full(userName) => GameRepository.findUserByUserName(userName) match {
				case Some(user) => user.isAdmin
				case _ => false
			}
			case _ => false
		}
	}

	def loggedInUserName(): Option[String] = {
		CurrentUserName.is match {
			case Full(name) => Some(name)
			case _ => None
		}
	}
	
}
