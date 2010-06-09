package at.idot.scimplbetter.model

import at.idot.scimplbetter.util.ImportData
import at.idot.scimplbetter.util.Importer
import at.idot.scimplbetter.util.GameRepository
import at.idot.scimplbetter.util.ExcelData

import javax.persistence._
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Assert._
import org.slf4j.{Logger,LoggerFactory}
import at.idot.scimplbetter.snippet.BetSelect


class GameRepositoryTest {
  private val log = LoggerFactory getLogger(getClass)

  @Test
  def loadData () = {
		val importer = new ImportData()
		importer.importGames()
	    importer.importDummyUsers()
	  
	    val games = GameRepository.allGames
	    assertEquals(48, games.size)
		val users = GameRepository.allUsers
		assertEquals(8, users.size)
  }	

  //@Test
  def exercising () = {

	  
    val user = GameRepository.findUserByUserName("username1")
    
    //no bets but all games
    val gamesWithBets = GameRepository.gamesAndBetsFromUser(user.get)                                         
    assertEquals("no bets:", 48, gamesWithBets.size)
    val actualBets = gamesWithBets.map(_._2).flatten //all with something now
    assertEquals("betsize 1", 0, actualBets.size)
 
    GameRepository.createBetsForGamesWithoutBetsForUser(GameRepository.findUserByUserName("username1").get)
    
    val gamesWithBets2 = GameRepository.gamesAndBetsFromUser(user.get)                                         
    assertEquals("after creation ", 48, gamesWithBets2.size)
    val actualBets2 = gamesWithBets2.map(_._2.get)
    assertEquals("betsize 2 ", 48, gamesWithBets2.size)
    
    //bet a little bit
    val bet = gamesWithBets2.head._2.get
    assertEquals(bet.user.userName , user.get.userName)
    assertEquals(bet.goalsTeam1, None)
    assertEquals(bet.goalsTeam2, None)
    assertFalse(bet.validBet)
    bet.goalsTeam1 = Some(2)
    bet.goalsTeam2 = Some(1)
    assertTrue(bet.validBet)
    
    val em = GameRepository.newEM
    em.mergeAndFlush(bet)
    em.close()
    
    
    //create again bets no change
    GameRepository.createBetsForGamesWithoutBetsForUser(GameRepository.findUserByUserName("username1").get)
    val gamesWithBets3 = GameRepository.gamesAndBetsFromUser(user.get)
    assertEquals("after 2. creation ", 48, gamesWithBets3.size)
    
    val bet2 = gamesWithBets3.head._2.get
    assertEquals(bet2.user.userName , user.get.userName)
    assertTrue(bet2.validBet)
    assertEquals(bet2.goalsTeam1, Some(2))
    assertEquals(bet2.goalsTeam2, Some(1))
    
    //calculate points //check saving and retrieving of points points per user
    //unit test points calculation for bets extra!

  }
  
  @Test
  def createExcel {

	  val excelData = new ExcelData()
	  val sheet = excelData.createExcelSheetComplete()
	  val stream = new java.io.FileOutputStream("testExcel.xls")
	  stream.write(sheet)
	  stream.close()
  }
  
	
}

