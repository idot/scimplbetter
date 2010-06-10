package at.idot.scimplbetter.util

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.List

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFSheet._
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle

import at.idot.scimplbetter.model.Game
import at.idot.scimplbetter.model.SpecialBet
import at.idot.scimplbetter.model.Team
import at.idot.scimplbetter.model.Player

class ExcelData {
	val creator = new UserRowCreator()
	val userRows = creator.createUserRows()
	creator.assignLeading(userRows)
	val games = GameRepository.allGames
	
	def createExcelSheetComplete(): Array[Byte] = {
	  var wb = new HSSFWorkbook()
	  fillSheet(wb, "all", 0)
	  fillSheet(wb, "points", 1)
	  fillSheet(wb, "cumulatedPoints", 2)
	  fillSheet(wb, "betFirstTeamGoals", 3)
	  fillSheet(wb, "betSecondTeamGoals", 4)
	  fillSheet(wb, "resultFirstTeamGoals", 5)
	  fillSheet(wb, "resultSecondTeamGoals", 6)
	  fillSpecialBets(wb, 7)
	  var out = new ByteArrayOutputStream()
	  wb.write(out)
	  out.close()
	  out.toByteArray
	}
	
	def fillSpecialBets(wb: HSSFWorkbook, nr: Int){
		val s = wb.createSheet()
//		 declare a row object reference
	//	val HSSFRow r = null
		//		 declare a cell object reference
	//	HSSFCell c = null
		//		 create 3 cell styles
		val userHeading = wb.createCellStyle()
		val pointsCell = wb.createCellStyle()
		pointsCell.setBorderRight(CellStyle.BORDER_THIN )
		userHeading.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"))
		userHeading.setBorderBottom(CellStyle.BORDER_THIN)
		wb.setSheetName(nr, "special Bets")

		val r = s.createRow(0)		
		r.setHeightInPoints(100)
		val headings = Seq("User","mvp","topScorer","champion","semifinalist","semifinalist","semifinalist","semifinalist")
		for((h,index) <- headings.zipWithIndex ){
			val c = r.createCell(index)
			c.setCellStyle( userHeading )
			c.setCellValue(new HSSFRichTextString(h))
		}
		for( (ur,rowNr) <- userRows.zipWithIndex.map(t => (t._1, t._2+1)) ){
			val r = s.createRow(rowNr)
			createRow(r, ur)	
		}
	}
	
	def createRow(row: HSSFRow, userRow: UserRow){
		val user = userRow.user 
		val c = row.createCell(0)
		c.setCellValue(userRow.user.userName)
		createCell(row, 1, userRow.user.specialBet.mvp )
		createCell(row, 2, userRow.user.specialBet.topscorer )
		createCell(row, 3, userRow.user.specialBet.winningTeam )
		createCell(row, 4, userRow.user.specialBet.semifinal1 )
		createCell(row, 5, userRow.user.specialBet.semifinal2 )
		createCell(row, 6, userRow.user.specialBet.semifinal3 )
		createCell(row, 7, userRow.user.specialBet.semifinal4 )
	}
	
	def createCell(row: HSSFRow, index: Int, player: Player){
		val c = row.createCell(index)
		val name = if(player == null) "" else player.lastName
		c.setCellValue(name)
	}
	

	def createCell(row: HSSFRow, index: Int, team: Team){
		val c = row.createCell(index)
		val name = if(team == null) "" else team.name
		c.setCellValue(name)
	}
	
	
	def fillSheet(wb: HSSFWorkbook, sheetname: String, nr: Int){
		val s = wb.createSheet()
//		 declare a row object reference
	//	val HSSFRow r = null
		//		 declare a cell object reference
	//	HSSFCell c = null
		//		 create 3 cell styles
		val userHeading = wb.createCellStyle()
		val gameStyle = wb.createCellStyle()
        val pointsHeading = wb.createCellStyle()
        pointsHeading.setRotation(90)
        pointsHeading.setBorderBottom(CellStyle.BORDER_THIN)
        pointsHeading.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"))
		gameStyle.setRotation(70)
		gameStyle.setBorderBottom(CellStyle.BORDER_THIN)
		gameStyle.setAlignment(CellStyle.ALIGN_LEFT)
        gameStyle.setWrapText(true)
		val pointsCell = wb.createCellStyle()
		pointsCell.setBorderRight(CellStyle.BORDER_THIN )
		userHeading.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"))
		userHeading.setBorderBottom(CellStyle.BORDER_THIN)
		gameStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"))
        val leadingCell = wb.createCellStyle()
        leadingCell.setBorderRight(CellStyle.BORDER_THIN )
        leadingCell.setFillBackgroundColor(HSSFColor.AQUA.index)
        leadingCell.setFillPattern(CellStyle.FINE_DOTS)
		wb.setSheetName(nr, sheetname)

		val r = s.createRow(0)		
		r.setHeightInPoints(100)
		var c = r.createCell(0)
		c.setCellStyle( userHeading )
        c.setCellValue(new HSSFRichTextString("User"))
        c = r.createCell(1)
		c.setCellStyle( pointsHeading )
        c.setCellValue(new HSSFRichTextString("Points"))

		for( index <-  0 until games.size ){
			c = r.createCell((index + 2))
			c.setCellStyle( gameStyle )
			val game = games(index)
			val gameName = game.firstTeam.name + "-" + game.secondTeam.name + "\n" + game.resultPrettyPrint
			c.setCellValue( new HSSFRichTextString(gameName) )
		}
		for( (ur,rowNr) <- userRows.zipWithIndex.map(t => (t._1, t._2+1)) ){
			val r = s.createRow(rowNr)
			c = r.createCell(0)
			c.setCellValue(new HSSFRichTextString(ur.user.userName))		
			c = r.createCell(1)
            if( ur.leader){
                c.setCellStyle( leadingCell )
            }else{
                c.setCellStyle( pointsCell ) 
            }
			c.setCellValue(ur.user.points)
			for( index <- 0 until games.size ){
				c = r.createCell(index + 2)
				val cellValue = sheetname match {
					case "all" => ur.games.get(index)
					case "points" => ur.pointsPerGame.get(index)
					case "cumulatedPoints" => ur.cumulatedPoints.get(index)
					case "betFirstTeamGoals" => ur.firstGoals.get(index)
					case "betSecondTeamGoals" => ur.secondGoals.get(index)
					case "resultFirstTeamGoals" => ur.resultFirstTeam.get(index)
					case "resultSecondTeamGoals" => ur.resultSecondTeam.get(index)
				}
				c.setCellValue(new HSSFRichTextString( cellValue ))
			}
		}
        s.setColumnWidth(1, 256 * 4)
	}
	
}
