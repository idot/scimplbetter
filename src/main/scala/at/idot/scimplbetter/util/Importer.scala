package at.idot.scimplbetter.util

import at.idot.scimplbetter.model.Bet
import at.idot.scimplbetter.model.Player
import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.model.Result
import at.idot.scimplbetter.model.Level
import at.idot.scimplbetter.model.Country
import at.idot.scimplbetter.model.Game
import at.idot.scimplbetter.model.Team
import scala.collection.mutable.HashMap
import java.util.GregorianCalendar
import java.text.SimpleDateFormat
import java.util.Calendar


class Importer {
	//2010/11/06	16:00:00
	val sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
	val teams = new HashMap[String,Team]
	val countries = new HashMap[String,Country]
	                           
	
	def importDummyUsers(filePath: String): Seq[User] = {
		val users = for(line <- scala.io.Source.fromFile(new java.io.File(filePath)).getLines() ) yield userFromLine(line)
		users.toList
	}
	
	def importPlayers(filePath: String): Seq[Player] = {
		val players = for(line <- scala.io.Source.fromFile(new java.io.File(filePath)).getLines()) yield playerFromLine(line)
		players.toList.flatten.toList
	}
	

	
	def userFromLine(line: String): User = {
		val items = line.split("\t").map(_.trim)
		val userName = items(0)
		val email = items(3)
		val password = items(4)
		val user = User(userName, password, email)
		user.firstName = items(1)
		user.lastName = items(2)
		if(items(5) == "admin"){ user.isAdmin = true }
		if(!user.isAdmin){
			user.canBet = true
		}
		user
	}
	
	def importLevels(filePath: String): Unit = {
		val levels = for(line <- scala.io.Source.fromFile(new java.io.File(filePath)).getLines() drop 1) yield levelFromLine(line)
		Level.levels ++= levels
		Level.levelsMap ++= Level.levels.map(l => (l.level, l)).toList.toMap
		Level.levelsNrMap ++= Level.levels.map(l => (l.levelNr, l)).toList.toMap
	}
	
	def levelFromLine(line: String): Level = {
		val items = line.split("\t").map(_.trim)
		val level = new Level
		level.level = items(0)
		level.pointsExact = Integer.parseInt(items(1))
		level.pointsTendency = Integer.parseInt(items(2))
		level.levelNr = Integer.parseInt(items(3))
		level
	}
	
	def importGames(filePath: String): List[Game] = {
		val games = for(line <- scala.io.Source.fromFile(new java.io.File(filePath)).getLines() drop 1) yield gameFromLine(line)
		games.toList
	}
	
	def createTeam(name: String): Team = {
		teams.getOrElseUpdate(name,{
			val country = createCountry(name)
			val team = new Team
			team.name = name
			team.country = country
			team
		})
	}
	
	def getTeam(name: String): Team = { //should throw an exception if not found when importing
		teams.get(name) match {
			case Some(team) => team
			case _ => throw new RuntimeException("could not find team: " + name)
		}
	}
	
	def createCountry(name: String): Country = {
		countries.getOrElseUpdate(name,{
			val country = new Country
			country.countryName = name
			country
		})
	}

	def playerFromLine(line: String): Option[Player] = {
		try{
			val items = line.split("\t")
			if(items.size != 3){
				return None
			}
			if(items(1) != "player"){
				return None
			}
			val country = items(0).trim
			val names = items(2).split("\\s+")
			if(names.length > 2){
				throw new RuntimeException("player has middle or more names " + line)
			}
			val player = new Player
			player.lastName = if(names.length == 1) names(0) else names(1)
			player.firstName = names(0)
			player.team = getTeam(country)
			player.team.players.add(player)
			return Some(player)
		}
		catch {
	     	case e: Exception => println("error importing: " + line)
		}
		None
	}
	
	//Subject	Group	Start Date	Start Time	Nr	Venue
	def gameFromLine(line: String): Game = {
		val items = line.split("\t").map(_.trim)
		val teams = items(0).split("vs").map(_.trim)
		val team1 = createTeam(teams(0))
		val team2 = createTeam(teams(1))
		val group = items(1)
		val time = date(items(2),items(3))
		val nr = Integer.parseInt(items(4))
		val venue = items(5)
		
		val game = new Game
		game.firstTeam = team1
		game.secondTeam = team2
		game.level = Level.levelsMap.get("group").get
		game.date = time
		game.nr = nr
		game.venue = venue
		game.gameGroup = group
		game.result = new Result
		game
	}
	
	def date(day: String, time: String): Calendar = {
		val date = sdf.parse(day+" "+time)
        val calendar = new GregorianCalendar() //TODO timezone!
        calendar.setTime(date);
        calendar
	}
	

	def importBets(filePath: String) = {
		val bets = for(line <- scala.io.Source.fromFile(new java.io.File(filePath)).getLines()) yield betsFromLine(line)
		bets.toList
	}

	def betsFromLine(line: String): (String,Seq[Bet]) = {
		val items = line.split("\t")
		val userName = items.head
		val bets = for(item <- items.drop(1)) yield {
			val betItems = item.split(":")
			val game = new Game
			game.nr = betItems(0).toInt
			val bet = new Bet
			bet.game = game
			bet.goalsTeam1 = Some(betItems(1).toInt)
			bet.goalsTeam2 = Some(betItems(2).toInt)
			bet
		}
		(userName, bets.toList)
	}
	
	
}