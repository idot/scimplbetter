package at.idot.scimplbetter.util

import _root_.scala.xml.NodeSeq
import _root_.java.security._
import _root_.java.util._
import _root_.java.io._
import _root_.net.liftweb.common.Loggable

/**
 * copied from lift gravatars
 * add monsterid etc..
 */
object Gravatar extends Loggable {

  val defaultSize: Int = 42
  val defaultRating: String = "G"
  val avatarEndpoint: String = "http://www.gravatar.com/avatar/"
  val defaultView = "wavatar"
  /**
   * @param e The email address of the recipient
   */
  def apply(e: String): NodeSeq = url(e,defaultSize,defaultRating,defaultView)

  /**
   * @param e The email address of the recipient
   * @param s The square size of the output gravatar
   */
  def apply(e: String, s: Int): NodeSeq = url(e,s,defaultRating,defaultView)

  /** 
   * @param e The email address of the recipient
   * @param s The square size of the output gravatar
   * @param r The rating of the Gravater, the default is G
   */
  def apply(e: String, s: Int, r: String, view: String) = url(e,s,r, view)

  private def url(email: String, size: Int, rating: String, view: String): NodeSeq = {
	val realView = if(view == "none") "" else "&d="+view 
    html(avatarEndpoint + getMD5(email) + "?s=" + size.toString + "&r=" + rating+realView)
  }

  private def html(in: String): NodeSeq = {
    <img src={in} alt="Gravatar" align="middle"/>
  }

  private def getMD5(message: String): String = {
    val md: MessageDigest = MessageDigest.getInstance("MD5")
    val bytes = message.getBytes("CP1252")

    try {
      BigInt(1,md.digest(bytes)).toString(16)
    } catch {
      case a: NoSuchAlgorithmException => logger.error("[Gravater] No Algorithm.", a); ""
      case x: UnsupportedEncodingException => logger.warn("[Gravater] Unsupported Encoding.", x); ""
      case _ => logger.warn("[Gravater] Unknown error."); ""
    }
  }
}
