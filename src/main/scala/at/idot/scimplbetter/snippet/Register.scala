package at.idot.scimplbetter.snippet

import at.idot.scimplbetter.model.User
import _root_.scala.xml.{NodeSeq, Text}
import net.liftweb.widgets.tablesorter.TableSorter
import net.liftweb.http.js.JsCmds.Noop

import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.{RequestVar,S,SHtml}
import net.liftweb.common.{Box,Empty,Full}
import net.liftweb.common.Logger
import at.idot.scimplbetter.util.GameRepository
import java.util.regex.Pattern

class Register {
	val validUserName = Pattern.compile("\\w{3,15}")
	
	
	var userName: String = ""
	var email: String = ""
	var password: String = ""
	var confirmpassword: String = ""
	
	//todo: make check
	def strongPassword(password: String) = true
	
	
	def takenUserName(userName: String): Boolean = GameRepository.findUserByUserName(userName).isDefined
		
	def register(xhtml: NodeSeq): NodeSeq = {
		val loginUser = AccessControl.user
		if(!loginUser.isDefined || !loginUser.get.isAdmin ){
			S.error("only administrators can register users")
			S.redirectTo("/games")
		}
		
		def doRegister(){
			if(takenUserName(userName)){
				S.error("username","username is in use")
			}
			if(!validUserName.matcher(userName).matches){
				S.error("username","username wrong")
			}
			if(!BetSelect.validEmail(email)){
				S.error("email","email not valid")
			}
			if(password != confirmpassword){
				S.error("password","password and confirmation different")
				S.error("confirmation","password and confirmation different")
			}
			if(S.errors.size == 0){
				val user = User(userName, password, email)
				user.gravatarEmail = email
				user.canBet = false //will be able to bet after completing the instruction wizzard!
				user.registeredBy = loginUser.get.userName
				val em = GameRepository.newEM
				em.persistAndFlush(user)
				em.close()
				GameRepository.createBetsForGamesWithoutBetsForUser(user)
				S.notice("successfully registered "+userName)
			}
		}
		 
		
		 bind("register", xhtml, 
           "username" -> SHtml.text("", s => userName = s, ("size","20")),
           "email" -> SHtml.text("", s => email = s, ("size","40")),
           "password" -> SHtml.password("", s => password = s, ("size","20")), 
           "confirmpassword" -> SHtml.password("", s => confirmpassword = s, ("size","20")),
           "submit" -> SHtml.submit("submit", ()=> doRegister, BetSelect.button: _* ),
           "cancel" -> SHtml.submit("cancel", () => S.redirectTo("/games"), BetSelect.button: _* )
           ) 
		
	}
	
}


