package at.idot.scimplbetter.util

import org.hibernate.Hibernate
import net.liftweb.common.Logger
import at.idot.scimplbetter.model.Level
import org.scala_libs.jpa.LocalEMF
import org.scala_libs.jpa.ThreadLocalEM
import at.idot.scimplbetter.model.SpecialBet
import at.idot.scimplbetter.model.Team
import at.idot.scimplbetter.model.Bet
import at.idot.scimplbetter.model.Player
import at.idot.scimplbetter.model.User
import at.idot.scimplbetter.model.Game
import scala.collection.JavaConversions._
import net.liftweb.jpa.RequestVarEM 
import java.util.Calendar
import at.idot.scimplbetter.model.BetterSettings

object GameRepository extends LocalEMF("better2010",false) with ThreadLocalEM with Logger { //no user transaction
	   lazy val firstGameTime = getFirstGameTime()
	   	
	   def specialBetsClosed(): Boolean = {
	  	   BetterSettings.closed(firstGameTime, BetterSettings.now)
	   }
	
	   def allGames(): Seq[Game] = {
	  	   val em = newEM
	  	   val games = em.createQuery[Game]( "from Game as game order by game.nr" ).getResultList
	  	   em.close()
	  	   games
	   }
	
	  def allGamesWithNoneBet(): Seq[(Game,Option[Bet])] = {
	 	  val games = allGames()
	 	  games.map((_,None))
	  }
	   
	  def allUsersWithNoneBet(): Seq[(User,Option[Bet])] = {
		  val users = allUsersNonAdmin()
		  users.map((_,None))
	  }
	  
	   def allPlayers(): Seq[Player] = {
	  	   val em = newEM
	  	   val players = em.createQuery[Player]( "from Player as player inner join fetch player.team order by player.lastName" ).getResultList
	  	   em.close()
	  	   players
	   }
	   
	   
	   def betsForUser(user: User): Seq[Bet] = {
	  	   val em = newEM
	  	   val betsForUser = em.createQuery[Bet]("from Bet as bet join fetch bet.game where bet.user = :user order by bet.game.nr").setParameter("user", user).getResultList
	  	   em.close()
	  	   betsForUser
	   }
	     
	   /**
	   * fetches all games and the bets for the user/game if there exists one
	   * should not be more than one bet/game, could be 0 (but should not because of policy
	   * a)
	   * from Game as game left join fetch game.bets as bets with bets.user.id = :userid order by game.nr
	   * does not work needs filter from Hibernate
	   * b)
	   * games fetched, bets for user fetched. then seq is made.
	   * 
	   * when switching to ranges queries revert back to initializing the collection?
	   * 
	   */
	   def gamesAndBetsFromUser(user: User): Seq[(Game,Option[Bet])] = {
	  	   val em = newEM
	  	   val games = em.createQuery[Game]( "from Game as game order by game.nr" ).getResultList()
	  	   val betsForUser = em.createQuery[Bet]("from Bet as bet join fetch bet.game where bet.user = :user").setParameter("user", user).getResultList
	  	   val game2bet = betsForUser.map(b => (b.game, b)).toMap
	  	   em.close()
	  	   val gb = for(game <- games) yield (game, game2bet.get(game))
	  	   gb.toList
	   }
	     
	   def usersAndBetsFromGame(game: Game): Seq[(User,Option[Bet])] = {
	  	   val em = newEM
	  	   val users = em.createQuery[User]( "from User as user where user.isAdmin = false" ).getResultList()  	
	  	   val betsForGame = em.createQuery[Bet]("from Bet as bet join fetch bet.user where bet.game = :game").setParameter("game", game).getResultList
	  	   val user2bet = betsForGame.map(b => (b.user, b)).toMap
	  	   em.close()
	  	   val ub = for(user <- users) yield (user, user2bet.get(user))
	  	   ub.toList
	   }
	     
	     
	   def allUsers(): Seq[User] = {
	  	   val em = newEM
	  	   val users = em.createQuery[User]( "from User as user" ).getResultList
	  	   em.close()
	  	   users
	   }
	     
	     
	   def allUsersNonAdmin(): Seq[User] = {
	  	   val em = newEM
	  	   val users = em.createQuery[User]( "from User as user where user.isAdmin = false" ).getResultList
	  	   em.close()
	  	   users
	   }
	     
	   def openGames(): Seq[Game] = {
	  	    val em = newEM
	    	val games = em.createQuery[Game]( "from Game as game where game.result.setted = false" ).getResultList
	    	em.close()
	    	games
	   }
	     

	    
	   def betsForGameNr(gameNr: Int): Seq[Bet] = {
	  	   val em = newEM
	  	   val betsForGame = em.createQuery[Bet]("from Bet as bet where bet.game.nr = :gameNr").setParameter("gameNr", gameNr).getResultList
	  	   em.close()
	  	   betsForGame
	   }
	     
	   def findGameByNr(gameNr: Int) : Option[Game] = {
	  	   val em = newEM
	  	   val games = em.createQuery[Game]("from Game as game where game.nr = :gameNr").setParameter("gameNr", gameNr).getResultList
	  	   em.close()
	  	   games.headOption
	   }
	     
	   def nextGameNr(): Int  = {
	  	   10
	   }
	   
	   def allTeams(): Seq[Team] = {
	  	   val em = newEM
	  	   val teams = em.createQuery[Team]("from Team as team order by team.name").getResultList
	  	   em.close()
	  	   teams
	   }
	     
	   def allLevels(): Seq[Level] = {
	  	   Level.levels.toList 
	   }
	    
	   def levelNameToLevel(levelName: String): Level = {
	  	   Level.levelsMap.get(levelName).get  //TODO it has to be there but who knows
	   }
	   
	   def levelNrToLevel(levelNr: Int): Level = {
	  	   Level.levelsNrMap.get(levelNr).get  //TODO it has to be there but who knows
	   } 
	     
	   def teamByName(teamName: String): Team = {//TODO it has to be there but who knows
	  	   val em = newEM
	  	   val teams = em.createQuery[Team]("from Team as team where team.name = :teamName").setParameter("teamName", teamName).getResultList
	  	   em.close()
	  	   teams.head
	   }
	     
	   def findUserByUserName(userName: String): Option[User] = {
	  	   val em = newEM
	  	   val users = em.createQuery[User]("from User as user where user.userName = :userName").setParameter("userName", userName).getResultList
	  	   em.close()
	  	   if(users.size != 1){
	  	  	   None
	  	   }
	  	   else{
	  	  	   users.headOption
	  	   }
	   }
	   
	   def importPlayer(player: Player): Unit = {
	  	   val em = newEM
	  	   val teams = em.createQuery[Team]( "from Team as team where team.name = :name").setParameter("name", player.team.name).getResultList
	  	   if(teams.size == 1){
	  	  	   player.team = teams(0)
	  	  	   em.persist(player)
	  	   }
	  	   else{
	  	  	   throw new RuntimeException("could not find team for player: " + player.team.name + " " + player.lastName)
	  	   }
	  	   em.close()
	   }
	     
	     
	   def findPlayerByIDString(id: String): Option[Player] = {
	  	   val idint = Integer.parseInt(id)   
	  	   val em = newEM
	  	   val players = em.createQuery[Player]( "from Player as player where player.id = :id").setParameter("id", idint).getResultList
	  	   em.close()
	  	   if(players.size == 1){
	  	  	   Some(players(0))
	  	   }
	  	   else{
	  	  	   None
	  	   }	  	   
	   }
	   
       def findPlayersForTeam(team: Team): Seq[Player] = {
    	   val em = newEM
	  	   val players = em.createQuery[Player]( "from Player as player where player.team = :team").setParameter("team", team).getResultList
	  	   em.close()
	  	   players
       }
	     
       def allTeamsWithPlayers(): Seq[Team] = {
    	   val em = newEM
    	   val players = em.createQuery[Team]("from Team as team join fetch team.players order by team.name").getResultList
    	   em.close()
    	   players
       }
       
      //simple getters 
  //      def allBets(): Iterable[Bet]
  //      def games(): Iterable[Game]
        
        
//        
//      //specialised getters  
//        def specialBetForUser(user: User): SpecialBet
//	    def betsForGame(game: Game): Iterable[Bet]
//	    
//        
//        
//	    def gameDescription(game: Game): String
//	    
//	    def gamesStarted(): Boolean
//        def highestGameNr(): Int
//        def gameHasBets(game: Game): Boolean
//        
//        def evaluableGames(): Iterable[Game]
//        
//      //modifiers   
//        def createBetsForGame(game: Game)
//	    def createBetsForGamesWithoutBets(user: User)
//
	     
	     
	    def createBetsForGamesWithoutBetsForUser(user: User): Unit = {
	    	val em = newEM
            val mergedUser = em.merge(user)
                        //from Game as game inner join fetch game.bets where not exists( select game from Bet as bet inner join bet.game as game where bet.user.id  = ? ) and game.date > ? and game.result.setted = false order by game.nr
            	//shortened from backup
            val gamesWithoutBetsForUser = em.createQuery[Game]( "from Game as game where game not in ( select bet.game from Bet as bet where bet.user.id = :userid ) order by game.nr" )
                                .setParameter("userid", user.id)
                                .getResultList();
            for( game <- gamesWithoutBetsForUser ){
                 val bet = new Bet
                 bet.game = game
                 bet.user = mergedUser
                 game.bets.add(bet)
                 mergedUser.bets.add(bet)
                 em.persist(game)
            }
            em.close()
        }


	     def calculatePoints(): Unit = {
	    	 val em = newEM
	    	 val games = em.createQuery[Game]( "from Game as game where game.calculated = false AND game.result.setted = true" ).getResultList
             for(game <- games){
                  for(bet <- game.bets){
                       val points = bet.calculatePoints
                       bet.points = points match {
                	  	   case Some(points) => points
                	  	   case _ => 0
                	   }
                       val user = bet.user
                       user.points += bet.points
                       em.merge(user)
                  }
                  game.calculated = true
                  em.persist(game)
	         }	    	
	    	 em.close()
	     }
	     
	     
	     //TODO: recalculatePoints : recalculate all points for all games in case somebody fkd up the results entry.
	    	 
	    	
//        
//      //deleters  
//        def delete(game: Game)
        
        

 //       public String downloadExcel();

 //        public byte[] getBetChart(int gameNr);
  //      public String getBetChartName(int gameNr);
	
	     //for dummmy data
	     def setBetsForUser(userName: String, reqBets: Seq[Bet]): Unit = {
	    	 val em = newEM
	    	 for(reqBet <- reqBets){
		    	 val bets = em.createQuery[Bet]( "from Bet as bet where bet.user.userName = :userName and bet.game.nr = :gameNr" ).setParameter("userName",userName).setParameter("gameNr", reqBet.game.nr).getResultList
		    	 bets.headOption match {
		    		 case Some(bet) => bet.goalsTeam1 = reqBet.goalsTeam1; bet.goalsTeam2 = reqBet.goalsTeam2;  em.persistAndFlush(bet)
		    		 case None =>
		    	 }
	    	 }
	    	 em.close()
	     }
	     
	     //start of worldcup
	     def getFirstGameTime(): Calendar = {
	        val em = newEM
	  	    val games = em.createQuery[Game]("from Game as game order by game.date").setMaxResults(1).getResultList
	  	    em.close()
	  	    if(games.size > 0){
	  	    	games.head.date 
	  	    }
	  	    else{
	  	    	BetterSettings.now //anyway strange better close them
	  	    }
	     }
	     
	     def getMVPs(): Seq[Player] = {
	    	val em = newEM
	  	    val players = em.createQuery[Player]("select user.specialBet.mvp from User as user").getResultList
	  	    em.close()
	  	    players
	     }
	     
	     def getTopScorers(): Seq[Player] = {
	    	val em = newEM
	  	    val players = em.createQuery[Player]("select user.specialBet.topscorer from User as user").getResultList
	  	    em.close()
	  	    players
	     }
	     
	     def getChampions(): Seq[Team] = {
	    	val em = newEM
	  	    val teams = em.createQuery[Team]("select user.specialBet.winningTeam from User as user").getResultList
	  	    em.close()
	  	    teams
	     }
	
	     def getSemifinalists(): Seq[Team] = {
	    	val em = newEM
	  	    val s1 = em.createQuery[Team]("select user.specialBet.semifinal1 from User as user").getResultList
	  	    val s2 = em.createQuery[Team]("select user.specialBet.semifinal2 from User as user").getResultList
	  	    val s3 = em.createQuery[Team]("select user.specialBet.semifinal3 from User as user").getResultList
	  	    val s4 = em.createQuery[Team]("select user.specialBet.semifinal4 from User as user").getResultList
	  	    em.close()
	  	    s1 ++ s2 ++ s3 ++ s4
	     }
	     
}
