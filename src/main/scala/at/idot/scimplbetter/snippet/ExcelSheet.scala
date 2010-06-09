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
import at.idot.scimplbetter.util.ExcelData

object ExcelSheet {

	def matcher: LiftRules.DispatchPF = {
		case r @ Req("statistics" :: "excel" :: Nil, _, GetRequest) => () => serveExcel(r)
  	} 
	
	def serveExcel(r: Req): Box[LiftResponse] = {
	  val excelData = new ExcelData()
	  val sheet = excelData.createExcelSheetComplete()
	  Full(InMemoryResponse(sheet,  List("Content-Type" -> "application/vnd.ms-excel", "Content-Length" -> sheet.size.toString), Nil,200)) 
	 
	}
	
	
}