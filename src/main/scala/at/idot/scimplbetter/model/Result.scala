package at.idot.scimplbetter.model

import javax.persistence._

@Embeddable
@scala.SerialVersionUID(uid=389563721L)
@scala.serializable
class Result {
	 
     var goalsTeam1 :Int = _
     var goalsTeam2 :Int = _
     var setted: Boolean = _
     
     override def toString() = goalsTeam1 +":"+goalsTeam2
}

object Result {

	def apply(goalsTeam1: Int, goalsTeam2: Int): Result = {
		val result = new Result()
		result.setted = true
		result.goalsTeam1 = goalsTeam1
		result.goalsTeam2 = goalsTeam2
		result
	}
	
}
