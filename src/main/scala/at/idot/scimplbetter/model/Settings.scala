package at.idot.scimplbetter.model

import java.util.GregorianCalendar
import java.util.Calendar

trait Settings {
	
	def now: Calendar
	def closingMinutesToGame: Int
	
		//utility method
	def closed(query: Calendar, now: Calendar): Boolean = {
	   	 now.add(Calendar.MINUTE, closingMinutesToGame)
	   	 now.after(query) 
	}
	
}

object BetterSettings extends Settings {
	
//	lazy val defaultUserCustomization = _defaultUserCustomization
	
	
	/**
	 * 
	 * 
	 * @return current time/date
	 */
	def now = new GregorianCalendar()
	
	def defaultStylesheet = "default"
	def closingMinutesToGame = 60	
	
//	def _defaultUserCustomization {
//		new UserCustomization  {
//			var maxResults = defaultMaxResults
//			var stylesheet = defaultStylesheet
//		}
//	}


}

