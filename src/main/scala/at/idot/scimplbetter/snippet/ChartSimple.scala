package at.idot.scimplbetter.snippet

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
import at.idot.scimplbetter.util.ChartSomethingByCounts
import org.jfree.chart.{ChartFactory,ChartUtilities,JFreeChart}

import at.idot.scimplbetter.model.Team
import at.idot.scimplbetter.model.Player

object ChartsDispatch {
	
	def matcher: LiftRules.DispatchPF = {
		case r @ Req("chart" :: category :: count ::
                 Nil, _, GetRequest) => () => serveChart(category, count, r)
  	} 
	
	def serveChart(category: String, scount: String, r: Req): Box[LiftResponse] = {
		    val count = scount.toInt
			val chart = category match {
				case "mvp" => {
					val pl = GameRepository.getMVPs()
					new ChartSomethingByCounts(pl, (p:Player) => p.lastName, count).getChart
				}
				case "topscorer" => {
					val pl = GameRepository.getTopScorers()
					new ChartSomethingByCounts(pl, (p:Player) => p.lastName, count).getChart
				}
				case "champion" => {
					val te = GameRepository.getChampions()
					new ChartSomethingByCounts(te, (t:Team) => t.name, count).getChart
				}
				case "semifinals" => {
					val te = GameRepository.getSemifinalists()
					new ChartSomethingByCounts(te, (t:Team) => t.name, count).getChart
				}
				case _ => {
					ChartSomethingByCounts.dummy
				}
		    }
		    Full(InMemoryResponse(chart,  List("Content-Type" -> "image/png"), Nil,200)) 
	}
	
	
}


class ChartCounts {

	
	
}