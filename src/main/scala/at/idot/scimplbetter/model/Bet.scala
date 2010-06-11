package at.idot.scimplbetter.model

import org.hibernate.annotations.Type
import javax.persistence._


@Entity
@Table( name = "bets", uniqueConstraints = Array(new UniqueConstraint(columnNames=Array("user_bet", "game_bet")))) //only one bet for each user/game
@scala.SerialVersionUID(uid=82178490L)
@scala.serializable
class Bet {
		       
	@Id @GeneratedValue
	var id: Int = _
			   
	@Version
	var version: Int = _
			   
	@ManyToOne(optional=false)
	@JoinColumn(nullable=false, name="user_bet")
	var user: User = _
		       
	@ManyToOne(optional=false)
	@JoinColumn(nullable=false, name="game_bet")
	var game: Game = _
		       
	var points: Int = 0
		       
	@Type(`type`="at.idot.jpa.IntOptionUserType")
	var goalsTeam1: Option[Int] = None
		       
	@Type(`type`="at.idot.jpa.IntOptionUserType")
	var goalsTeam2: Option[Int] = None
		       
	var calculated: Boolean = false
		       
	
     def checkExact = {
    	  validBet && 
          goalsTeam1.get == game.result.goalsTeam1 && 
          goalsTeam2.get == game.result.goalsTeam2
     }
     
     def checkTendencyEquals = {
    	  validBet && 
    	  goalsTeam1.get == goalsTeam2.get &&
    	  game.result.goalsTeam1 == game.result.goalsTeam2
     }
     
     def checkTendencyTeam1Wins = {
    	  validBet &&
    	  goalsTeam1.get > goalsTeam2.get &&
    	  game.result.goalsTeam1 > game.result.goalsTeam2
     }
     
     def checkTendencyTeam2Wins = {
    	  validBet &&
    	  goalsTeam1.get < goalsTeam2.get &&
    	  game.result.goalsTeam1 < game.result.goalsTeam2
     }
     
     
     /**
     * calculates the points and sets them
     * 
     * @return
     */
    def calculatePoints(): Option[Int] = {
    	if(!game.result.setted){
    		return None
    	}
    	val calc =  if(validBet){
    		 if(checkExact){
    			 game.level.pointsExact
    		 }
    		 else if(checkTendencyEquals || checkTendencyTeam1Wins || checkTendencyTeam2Wins ){ 
    			 game.level.pointsTendency
    		 }
    		 else{
    			 0
    		 }
    	  }
    	  else{
    	 	  0
    	}
    	Some(calc)
     }     
     
     def validBet = goalsTeam1.isDefined && goalsTeam2.isDefined
     
     def betDisplay = goalsTeam1+":"+goalsTeam2
     
     def pointsDisplay = if(calculated) points.toString else "NA"
     
     def canChange(ouser :Option[User]): Boolean = {
    	 if(game.closed) return false
    	 ouser match {
    		 case Some(user) => user == this.user && user.canBet
    		 case None => false
    	 }
     }
     
     override def toString: String = id + "\t" + user.userName + "\t" + game.nr + "\t" + goalsTeam1 + ":" + goalsTeam2
	
     def betDisplayPretty = pretty(goalsTeam1)+":"+pretty(goalsTeam2)
//TODO     
//     @PrePersist
//     @PreUpdate
//     def preventUpdate(bet: Bet)
//     
    	 
      def pretty(b: Option[Int]) = b.map(_.toString).getOrElse("-")	 
    	 
}


object Bet {
	
	def apply(user: User, game: Game): Bet = {
		val bet = new Bet
		bet.game = game
		bet.user = user
		bet
	}
	
	
}

