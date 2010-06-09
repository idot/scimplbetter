package at.idot.scimplbetter.comet

import _root_.net.liftweb._
import http._
import common._
import actor._
import util._
import Helpers._
import _root_.scala.xml._
import S._
import SHtml._
import js._
import JsCmds._
import JE._
import net.liftweb.http.js.jquery.JqJsCmds.{AppendHtml,PrependHtml}
import at.idot.scimplbetter.snippet.AccessControl
import at.idot.scimplbetter.util.GameRepository
import at.idot.scimplbetter.util.Gravatar

/***
* taken from examples. with trivial changes
*
*/
class Chat extends CometActor with CometListener {
  private var userName = ""
  private var gravatarEmail = ""//complete link
  private var gravatarDefault = ""
  private var chats: List[ChatLine] = Nil
  private lazy val infoId = uniqueId + "_info"
  private lazy val infoIn = uniqueId + "_in"
  private lazy val inputArea = findKids(defaultXml, "chat", "input")
  private lazy val bodyArea = findKids(defaultXml, "chat", "body")
  private lazy val singleLine = deepFindKids(bodyArea, "chat", "list")


  
  
  // handle an update to the chat lists
  // by diffing the lists and then sending a partial update
  // to the browser
  override def lowPriority = {
    case ChatServerUpdate(value) =>
      val update = (value -- chats).reverse.map(b => PrependHtml(infoId, line(b)))
      partialUpdate(update)
      chats = value
  }

  
  // send a message to the chat server
  private def sendMessage(msg: String) = ChatServer ! ChatServerMsg(gravatarEmail,gravatarDefault, userName, msg.trim)

  // display a line
  private def line(c: ChatLine) = bind("list", singleLine,
		  							   "gravatar" -> <span>&nbsp;{ Gravatar(c.gravatarEmail, 30, "r", c.gravatarDefault ) }&nbsp;</span>,
                                       "when" -> hourFormat(c.when),
                                       "who" -> <a href={"/user/"+c.user}>{c.user}</a>,
                                       "msg" -> c.msg)

  // display a list of chats
  private def displayList(in: NodeSeq): NodeSeq = chats.reverse.flatMap(line)

  def renderInput(inputLine: NodeSeq): NodeSeq = {
	  if(userName != ""){
		  ajaxForm(After(100, SetValueAndFocus(infoIn, "")),
			bind("chat", inputLine,
                "input" -> text("", sendMessage _, "id" -> infoIn, ("size","70")),
                "submit" -> <input type="submit" value="Post It" onmouseover="this.className='bb bbhov'" onmouseout="this.className='bb'" class="bb"/>))
        
	  }
	  else{
	 	   bind("chat", inputLine,
	 	  	   "input" -> "you need to register and log in to post",
	 	  	   "submit" -> NodeSeq.Empty)
	  }
  }
  
  
  // render the whole list of chats
  override def render = {
      bind("chat", bodyArea,
       AttrBindParam("id", Text(infoId), "id"),
       "inputLine" -> renderInput _,
       "list" -> displayList _)
  }
  
  // setup the component
  override def localSetup {
    super.localSetup
  }

  // register as a listener
  def registerWith = ChatServer

  def setUser(userName: String) {
    GameRepository.findUserByUserName(userName) match {
    	case Some(user) =>  {
    		this.userName = userName
    		gravatarEmail = user.gravatarEmail 
    		gravatarDefault = user.gravatarDefault
        }
    	case None => {
    		this.userName = ""
			this.gravatarEmail = ""
			this.gravatarDefault = "monsterid"
    	}
     }
     reRender(true)
  }
  


}


