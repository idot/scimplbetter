package at.idot.scimplbetter.util


object BetResult extends Enumeration {
     type BetResult = Value
     val Equal, Team1, Team2, Invalid = Value
}


class ResultsCalculator {

//	def toResult(points1: Option[Int], points2: Option[Int]): BetResult = {
//		(points1, points2) match {
//			case (None,_) => Invalid
//			case (_, None) => Invalid
//			case (Some(p1),Some(p2)) if p1 == p2 => Equal
//			case (Some(p1),Some(p2)) if p1 > p2 => Team1
//			case (Some(p1),Some(p2)) if p2 > p1 => Team2
//		}
//	}
//
//	def fromBet(bet: Bet): BetResult = {
//		toResult(bet.goalsTeam1, bet.goalsTeam2)		
//	}
//	
//	def fromResult(result: Result): BetResult = {
//		if(!result.setted){
//			new Invalid
//		}
//		else{
//			toResult(Some(result.goalsTeam1),Some(result.goalsTeam2))
//		}
//	}
//	
//	def exact(bet: Bet, result: Result): Boolean = {
//		bet.validBet && result.setted && bet.goalsTeam1.get == result.goalsTeam1 && bet.goalsTeam2.get == result.goalsTeam2
//	}
//	      
//	def calculatePoints(bet: Bet, result: Result, level: Level): Option[Int] = {
//		if(exact(bet, result)){
//			Some(level.pointsExact)
//		}
//		else{
//			val betResult = fromBet(bet)
//			val gameResult = fromResult(result)
//			if(betResult != Invalid && betResult == gameResult){
//				Some(level.pointsTendency)
//			}
//			else{
//				None
//			}
//		}
//	}

	
	
}