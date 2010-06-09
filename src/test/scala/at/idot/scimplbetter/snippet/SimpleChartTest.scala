package at.idot.scimplbetter.snippet


import javax.persistence._
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Assert._
import org.slf4j.{Logger,LoggerFactory}



class SimpleChartTest {
  private val log = LoggerFactory getLogger(getClass)

	

  @Test
  def exercising () = {
	  val bets = List(None, None, Some(1), Some(2), Some(3), Some(2))
	  val result = SimpleChart.tabulate(bets)
	  val expected = Map(0-> 0, 1 -> 1, 2->2, 3->1) ++ (4 to 13).map((_,0)) 
      assertEquals(expected, result)                                       

  }
	
}