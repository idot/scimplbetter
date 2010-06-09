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

class Login {
	
	
		
	def login(xhtml: NodeSeq) : NodeSeq = { 
      var userName = ""
      var password = ""
	  
      def doLogin() = {
    	  GameRepository.findUserByUserName(userName) match {
    	 	  case Some(user) => {
    	 	 	   if(user.authenticate(password)){
    	 	 	  	   AccessControl.login(user)
    	 	 	  	   user.invalidLogins = 0
    	 	 	  	   S.notice("  "+userName+" successfully logged in")
    	 	 	  	   if(!user.hadInstructions && !user.isAdmin){
    	 	 	  	  	   S.notice("detailsNotice",Text("you have to fill out and submit this form in order to be able to bet."))
    	 	 	  	  	   S.redirectTo("/myHome")
    	 	 	  	   }
    	 	 	  	   else{
    	 	 	  	  	   S.redirectTo("/games/"+userName)
    	 	 	  	   }
    	 	 	   }
    	 	 	   else{
    	 	 	  	   user.invalidLogins += 1
    	 	 		   S.error("  Could not log you in")
    	 	 	   }   	 	 	  
    	 	  }
    	 	  case _ =>  { 
    	 	 	  S.error("  Could not log you in")
    	 	  }
    	  } 
      }
	   
      bind("login", xhtml, 
           "username" -> SHtml.text(userName, s => userName = s, ("size","20")),
           "password" -> SHtml.password(password, s => password = s, ("size","20")), 
           "submit" -> SHtml.submit("log in", doLogin _, BetSelect.button: _* ),
           "cancel" -> SHtml.submit("cancel", () => S.redirectTo("/games"), BetSelect.button: _* )
//TODO:    "resetPassword" -> SHtml.submit("reset password", () => S.redirectTo("authentication/resetRequest"), BetSelect.button: _* ) //TODO: => resetRequest
           ) 
    } 
	
	
	
	def changePassword(xhtml: NodeSeq): NodeSeq = {
        var password = ""
		var newpassword = ""
	    var confirmpassword = ""
	    	
		val loginUser = AccessControl.user
		if(!loginUser.isDefined){
			S.error("you must be logged in to change the password")
			S.redirectTo("/games") //resetPassword?
		}
        
        def doChange(): Unit = {
        	loginUser match {
        		case Some(user) => {
        			if(!user.authenticate(password)){
        				S.error("oldpassword","wrong old password")
        			}
        			if(newpassword != confirmpassword){
        				S.error("newpassword","password and confirmation different")
        				S.error("confirmpassword","password and confirmation different")
        			}
        			if(S.errors.size == 0){
        				val em = GameRepository.newEM
        				val merged = em.merge(user)
        				merged.password = newpassword
						em.persistAndFlush(merged)
						em.close()
						S.notice("password successfully changed")
						S.redirectTo("/games")
        			}
        		}
        		case _ => { //again
        			S.error("you must be logged in to change the password")
        			S.redirectTo("/games") //should redirect to resetPassword?
        		}
        	}
        }
        
        
        
		bind("changePassword",xhtml,
		   "password" -> SHtml.password("", s => password = s, ("size","20")), 
		   "newpassword" -> SHtml.password("", s => newpassword = s, ("size","20")), 
           "confirmpassword" -> SHtml.password("", s => confirmpassword = s, ("size","20")),
           "submit" -> SHtml.submit("submit", doChange _ , BetSelect.button: _* ),
           "cancel" -> SHtml.submit("cancel", () => S.redirectTo("/games") , BetSelect.button: _* )
		)
			
	}



}


