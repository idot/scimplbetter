package at.idot.scimplbetter.model


import at.idot.scimplbetter.util.ImportData
import at.idot.scimplbetter.util.Importer
import at.idot.scimplbetter.util.GameRepository


import javax.persistence._
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Assert._
import org.slf4j.{Logger,LoggerFactory}
import java.util.Calendar
import java.util.GregorianCalendar

class BetTest {
  private val log = LoggerFactory getLogger(getClass)

  case class TestSettings(now: Calendar, closingMinutesToGame: Int) extends Settings

  @Test
  def testBets () = {
	  val user = new User
	  val now = new GregorianCalendar()
	  val gameNow = new Game { override def settings = TestSettings(now, 60) }
	  val bet = Bet(user, gameNow)
	  assertFalse(bet.canChange(Some(user)))
	  assertFalse(checkBet(bet, None, None, bet.checkExact _))
	  
	  gameNow.result = Result(0,0)
	  
	  assertFalse(checkBet(bet, None, None, bet.checkExact _))
	  assertFalse(checkBet(bet, Some(1), Some(1), bet.checkExact _))
	  assertTrue(checkBet(bet, Some(0), Some(0), bet.checkExact _))
	  assertTrue(checkBet(bet, Some(0), Some(0), bet.checkTendencyEquals _)) //! order of test in points important
	  assertTrue(checkBet(bet, Some(1), Some(1), bet.checkTendencyEquals _)) //! order of test in points important
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam2Wins _))
	  assertFalse(checkBet(bet, Some(1), Some(2), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(2), Some(1), bet.checkTendencyTeam2Wins _))
	  
	  gameNow.result = Result(1,1)
	  assertFalse(checkBet(bet, None, None, bet.checkExact _))
	  assertTrue(checkBet(bet, Some(1), Some(1), bet.checkExact _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkExact _))
	  assertTrue(checkBet(bet, Some(0), Some(0), bet.checkTendencyEquals _))//! order of test in points important
	  assertTrue(checkBet(bet, Some(1), Some(1), bet.checkTendencyEquals _)) //! order of test in points important
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam2Wins _))
	  assertFalse(checkBet(bet, Some(1), Some(2), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(2), Some(1), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(1), Some(2), bet.checkTendencyTeam2Wins _))
	  assertFalse(checkBet(bet, Some(2), Some(1), bet.checkTendencyTeam2Wins _))
	  
	  gameNow.result = Result(2,3)
	  assertFalse(checkBet(bet, None, None, bet.checkExact _))
	  assertFalse(checkBet(bet, Some(1), Some(1), bet.checkExact _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkExact _))
	  assertTrue(checkBet(bet, Some(2), Some(3), bet.checkExact _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyEquals _))
	  assertFalse(checkBet(bet, Some(1), Some(1), bet.checkTendencyEquals _)) 
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam2Wins _))
	  assertFalse(checkBet(bet, Some(1), Some(2), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(2), Some(1), bet.checkTendencyTeam1Wins _))
	  assertTrue(checkBet(bet, Some(1), Some(2), bet.checkTendencyTeam2Wins _))
	  assertFalse(checkBet(bet, Some(2), Some(1), bet.checkTendencyTeam2Wins _))
	  
	  gameNow.result = Result(3,2)
	  assertFalse(checkBet(bet, None, None, bet.checkExact _))
	  assertFalse(checkBet(bet, Some(1), Some(1), bet.checkExact _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkExact _))
	  assertTrue(checkBet(bet, Some(3), Some(2), bet.checkExact _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyEquals _))
	  assertFalse(checkBet(bet, Some(1), Some(1), bet.checkTendencyEquals _)) 
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(0), Some(0), bet.checkTendencyTeam2Wins _))
	  assertFalse(checkBet(bet, Some(1), Some(2), bet.checkTendencyTeam1Wins _))
	  assertTrue(checkBet(bet, Some(2), Some(1), bet.checkTendencyTeam1Wins _))
	  assertFalse(checkBet(bet, Some(1), Some(2), bet.checkTendencyTeam2Wins _))
	  assertFalse(checkBet(bet, Some(2), Some(1), bet.checkTendencyTeam2Wins _))
	  
  }

  def checkBet(bet: Bet, points1: Option[Int], points2: Option[Int],f: () => Boolean): Boolean = {
	  bet.goalsTeam1 = points1
	  bet.goalsTeam2 = points2
	  f()
  }
  
}