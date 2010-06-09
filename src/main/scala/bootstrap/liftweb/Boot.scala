package bootstrap.liftweb

import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.util.ImportData
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.at.idot.scimplbetter.model
import at.idot.scimplbetter.util.GameRepository
import net.liftweb.widgets.tablesorter.TableSorter
import at.idot.scimplbetter.snippet.Games
import at.idot.scimplbetter.snippet.Users
import at.idot.scimplbetter.snippet.AccessControl
import net.liftweb.util.Helpers._
import at.idot.scimplbetter.snippet.BetStatsChart
import at.idot.scimplbetter.snippet.ExcelSheet
import at.idot.scimplbetter.model.BetterSettings

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
	  
	//we set the correct timezone right from the start!!
    System.setProperty("user.timezone","Europe/Vienna")
    System.setProperty("user.timezone","Europe/Vienna")
    printCurrentTime()
//uncomment importDummy() to import the games and users from the files directory
    importDummy()
    
    
    
    // where to search snippet
    LiftRules.addToPackages("at.idot.scimplbetter")
    
    val statistics = 
		Menu(Loc("graph",List("statistics","all"), "graph")) ::
		Menu(Loc("excel",List("statistics","excel"), "excel" )) :: Nil

    // Build SiteMap
    val entries = 
    	 Menu(Loc("Home", List("index"), "all games", Hidden)) ::
    	 Menu(Loc("usersBets",List("myBets"),usersBetText(),If(() => AccessControl.loggedIn,() => new RedirectResponse("/showGames")))) ::
    	 Menu(Loc("usersDetailsLink",List("myHome"),usersHomeText(),If(() => AccessControl.loggedIn,() => new RedirectResponse("/showGames")))) ::
    	 Menu(Loc("register",List("authentication","register"),"register",If(AccessControl.isAdmin,() => new ForbiddenResponse("you have to be logged in as admin")))) ::
    	 Menu(Loc("login", List("authentication", "login"), "login",If(() => !AccessControl.loggedIn, () => new ForbiddenResponse("You are already logged in")))) ::
    	 Menu(Loc("logout", List("logout"),logoutText(),If(() => AccessControl.loggedIn, () => new ForbiddenResponse("You are not logged in")))) ::
    	 Menu(Loc("changePassword", List("authentication", "changePassword"), "login",Hidden)) ::
  //  	 Menu(Loc("login", List("authentication", "resetRequest"), "login",!AccessControl.loggedIn)) :: //not logged in -> request new password!
    	 Menu(Loc("allGames", ("games" :: Nil), "games")) ::
    	 Menu(Loc("allUsers", ("users" :: Nil), "users")) ::
    	 Menu(Loc("allGamesHidden", ("showGames" :: Nil) -> true, "gamesHidden",Hidden)) ::
    	 Menu(Loc("allUsersHidden", ("showUsers" :: Nil) -> true, "usersHidden",Hidden)) ::
    	 Menu(Loc("userDetailsHome", ("showUser" :: Nil) -> true, "userHome", Hidden)) ::
    	 Menu(Loc("editgame", ("showGame" :: "game" :: Nil) -> true, "editgame", Hidden)) :: //TODO: edit game 
    	 Menu(Loc("statistics", ("statistics" :: Nil) -> true, "statistics"), statistics: _*) ::
    	 Menu(Loc("chat", ("chat" :: "index":: Nil), "chat")) ::
  //       Menu(Loc("Static", Link(List("static"), true, "/static/index"),"about")) :: 
	     Menu(Loc("404", List("404"), "404", Hidden)) :: Nil

	     
	

	     
	val gamesRewrite: LiftRules.RewritePF = NamedPF("EditGameRewrite"){
    	 case RewriteRequest(ParsePath("index" :: _, _, _, _), _, _) =>
		    RewriteResponse("showGames" :: "all" :: Nil)
    	 case RewriteRequest(ParsePath("game" :: gameNr :: _, _, _, _), _, _) =>    //??
		    RewriteResponse("showGame" :: "game" :: Nil, Map("gameNr" -> gameNr))  
		 case RewriteRequest(ParsePath("games" :: userName :: _,_,_,_),_,_) =>                   //all games + bets for user
		    RewriteResponse("showGames" :: "all" :: Nil, Map(Games.requestedUser -> userName))
		 case RewriteRequest(ParsePath("games" ::  _,_,_,_),_,_) =>								//all games
		    RewriteResponse("showGames" :: "all" :: Nil)
		 case RewriteRequest(ParsePath("users" :: gamesName :: _,_,_,_),_,_) =>					 //all users + bets for game
		    RewriteResponse("showUsers" :: "all" :: Nil, Map(Users.requestedGame -> gamesName))
		 case RewriteRequest(ParsePath("users" ::  _,_,_,_),_,_) =>								 //all users
		    RewriteResponse("showUsers" :: "all" :: Nil)
		 case RewriteRequest(ParsePath("user" :: userName :: _,_,_,_),_,_) =>					 //user home 
		    RewriteResponse("showUser" :: "details" :: Nil, Map("requestedUser" -> userName))
	
    }     
	     
    LiftRules.dispatch.prepend {
    	case (r @ Req("logout" :: Nil, _, _)) => () => {
    		AccessControl.logout
    		Full(RedirectResponse("/games"))
    	}
    	case (r @ Req("myBets":: Nil, _, _)) => () => {
    		 AccessControl.loggedInUserName match {
	    		 case Some(name) => Full(RedirectResponse("/games/"+name))
	    		 case _ => Full(RedirectResponse("/games"))
    		 }
    	}
    	case (r @ Req("myHome":: Nil, _, _)) => () => {
    		 AccessControl.loggedInUserName match {
	    		 case Some(name) => Full(RedirectResponse("/user/"+name))
	    		 case _ => Full(RedirectResponse("/games"))
    		 }
    	}
    }
    
	TableSorter.init()    
	     
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => AccessControl.loggedIn)

    LiftRules.rewrite.prepend(gamesRewrite)
    
    LiftRules.dispatch.append(BetStatsChart.matcher) 
    LiftRules.dispatch.append(ExcelSheet.matcher)
 
  }

  def printCurrentTime(){
	  val time = "current java time (timezone) is: " + new java.util.Date() + ". Seems like you can change this in Boot"
	  val string = "**********************\n**"+time+"****\n***********************\n"
	  System.out.println(string)
  }
  
  def importDummy(){
	val importer = new ImportData()
    importer.importGames()
    importer.importDummyUsers()
  }
  
  def logoutText(): String = {
 	  "logout " + AccessControl.loggedInUserName.get
  }
  
  def usersBetText(): String = {
	  AccessControl.loggedInUserName.get + "'s bets"
  }
  
  def usersHomeText(): String = {
	  AccessControl.loggedInUserName.get + "'s home"
  }
  
 //fadeout lifts notices
   LiftRules.noticesAutoFadeOut.default.set(Vendor((noticeType: NoticeType.Value) => Full((2 seconds, 2 seconds))))

  
  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
