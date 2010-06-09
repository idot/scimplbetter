package at.idot.scimplbetter.util



class ImportData {

   def importGames() {
	val em = GameRepository.newEM
    val importer = new Importer()
    importer.importLevels("files/levels.txt")
    val games = importer.importGames("files/fifa_2010.tab")
    games.foreach(em.persist(_))
    em.close()
    val players = importer.importPlayers("files/players_country.txt")
    players.foreach(GameRepository.importPlayer(_))
  }
  
  def importDummyUsers(){
	val importer = new Importer()
    val em = GameRepository.newEM
    val timporter = new Importer()
	val users = importer.importDummyUsers("files/dummyUsers.txt")
    users.foreach(em.persist(_))
    em.close()
	createBetsForDummyUsers()
	val bets = importer.importBets("files/dummyBets.txt")
	for(bet <- bets){
		GameRepository.setBetsForUser(bet._1, bet._2)
	}
  }
  
  def createBetsForDummyUsers(){
	 for(user <- GameRepository.allUsersNonAdmin){
		 GameRepository.createBetsForGamesWithoutBetsForUser(user)
	 }
  }
  

  	
	
	
}