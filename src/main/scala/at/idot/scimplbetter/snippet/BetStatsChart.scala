package at.idot.scimplbetter.snippet

import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.util.ImportData
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.at.idot.scimplbetter.model
import at.idot.scimplbetter.util.GameRepository
import net.liftweb.widgets.tablesorter.TableSorter
import net.liftweb.util.Helpers._
import java.awt.Image
import java.awt.image.BufferedImage

import org.jfree.data.general.DefaultKeyedValues2DDataset
import org.jfree.chart.{ChartFactory,ChartUtilities,JFreeChart}
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.general.DefaultPieDataset
import org.jfree.chart.plot.{PiePlot,PlotOrientation}
import org.jfree.chart.title.TextTitle
import org.jfree.chart.labels.{StandardCategoryItemLabelGenerator,StandardPieSectionLabelGenerator}
import org.jfree.chart.ChartPanel
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.renderer.category.BarRenderer
import java.awt.GradientPaint
import java.awt.Color
import org.jfree.chart.renderer.category.StandardBarPainter

object BetStatsChart {
	val dim = 150
	object BetStatsChart {
		def unapply(in: String): Some[Array[Byte]] = {
			tryo(in.toInt) match {
				case Full(nr) => { 
					val bets = GameRepository.betsForGameNr(nr)
					val bet1 = bets.map(_.goalsTeam1)
					val bet2 = bets.map(_.goalsTeam2)
					val charto = new SimpleChart(bet1, bet2, "team1", "team2")
					val chartp = charto.createChart
					Some(ChartUtilities.encodeAsPNG(chartp))
				}
				case _ => {
					val default = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB)
					Some(ChartUtilities.encodeAsPNG(default))
				}
			}
		}
	}
	
	def matcher: LiftRules.DispatchPF = {
		case r @ Req("betstats" :: BetStatsChart(img) ::
                 Nil, _, GetRequest) => () => serveImage(img, r)
  	} 
	
	def serveImage(image: Array[Byte], r: Req): Box[LiftResponse] = {
		Full(InMemoryResponse(image,  List("Content-Type" -> "image/png"), Nil,200)) 
	}
	

	
}



class SimpleChart(betsTeam1: Seq[Option[Int]], betsTeam2: Seq[Option[Int]], team1: String, team2: String) {
	var bets1 = SimpleChart.tabulate(betsTeam1)
	var bets2 = SimpleChart.tabulate(betsTeam2)
	
	
	
	def createDataSet() = {
		val data = new DefaultKeyedValues2DDataset()
		val indices = bets1.keys.toList.sorted.reverse
		for(ind <- indices){
			bets1.get(ind) match{ 
				case Some(c) => data.addValue(-c, team1, ind.toString)
				case _ => 
			}
			bets2.get(ind) match{ 
				case Some(c) => data.addValue(c, team2, ind.toString)
				case _ =>
			}
		}
        data
	}
	 
	def createChart() = {
		var chart = ChartFactory.createStackedBarChart("", "", "", createDataSet(), PlotOrientation.HORIZONTAL, false, false, false)
		val plot = chart.getPlot.asInstanceOf[CategoryPlot]
		plot.setBackgroundPaint(Color.white)
		plot.setOutlineVisible(false)
		val xist = plot.getRangeAxis()
		xist.setVisible(false)
		val renderer = plot.getRenderer().asInstanceOf[BarRenderer]
		renderer.setBarPainter(new StandardBarPainter())
		renderer.setShadowVisible(false)
		renderer.setDrawBarOutline(false)
		 chart.createBufferedImage(BetStatsChart.dim,BetStatsChart.dim)
	}
	
	
}


object SimpleChart {
	def tabulate(bets: Seq[Option[Int]]): Map[Int,Int] = {
		val betsMap = bets.flatten.groupBy(b => b)
		var betsMapCount = betsMap.mapValues(_.size)
		var missing =  (0 to 13).toList.filterNot(betsMapCount.contains(_)).map((_,0))
		(betsMapCount ++ missing).toMap
	}
}


object doChart {
	
    def main(ars: Array[String]){
    	//val seq = List(1,2,3,4,1,2,3,1,2,12,3,3,1,2,0,0,0).map(Some(_))
    	val seq1 = List(Some(5),Some(3),Some(2),Some(4))
    	val seq2 = List(Some(1))
    	val charto = new SimpleChart(seq1,seq2, "team1","team2")
    	val chart = charto.createChart
		val png = ChartUtilities.encodeAsPNG(chart)
		val wr = new java.io.FileOutputStream("testFile.png")
    	wr.write(png)
    	wr.close
    
    }
}
