package at.idot.scimplbetter.comet


import _root_.net.liftweb._
import http._
import common._
import actor._
import util._
import Helpers._
import _root_.scala.xml.{NodeSeq, Text}
import textile.TextileParser
import _root_.java.util.Date

/**
 * Taken from the examples
 * removed the license hmm....?
 * 
 * A chat server.  It gets messages and returns them
 */

object ChatServer extends LiftActor with ListenerManager {
  private var chats: List[ChatLine] = List()

  override def lowPriority = {
    case ChatServerMsg(gravatarEmail, gravatarDefault, user, msg) if msg.length > 0 =>
      chats ::= ChatLine(gravatarEmail, gravatarDefault, user, toHtml(msg), timeNow)
      chats = chats.take(50)
      updateListeners()

    case _ =>
  }

  def createUpdate = ChatServerUpdate(chats.take(15))

  /**
   * Convert an incoming string into XHTML using Textile Markup
   *
   * @param msg the incoming string
   *
   * @return textile markup for the incoming string
   */
  def toHtml(msg: String): NodeSeq = TextileParser.paraFixer(TextileParser.toHtml(msg, Empty))

}

case class ChatLine(gravatarEmail: String, gravatarDefault: String, user: String, msg: NodeSeq, when: Date)
case class ChatServerMsg(gravatarEmail: String, gravatarDefault: String, user: String, msg: String)
case class ChatServerUpdate(msgs: List[ChatLine])



