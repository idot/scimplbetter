package at.idot.scimplbetter.model

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import javax.persistence._

@Embeddable
@scala.SerialVersionUID(uid=483736591L)
@scala.serializable
class Level { //see scala jpa demo for enum
	
	var level :String = _//groups, quarter final, semi final, final
	var pointsExact :Int = _
	var pointsTendency :Int = _
	var levelNr :Int = _//1,2,3,4
	
	
}


object Level {
	
	val levels = new ListBuffer[Level]()
	
	val levelsMap = new HashMap[String,Level]()
	
	val levelsNrMap = new HashMap[Int,Level]()
	
}