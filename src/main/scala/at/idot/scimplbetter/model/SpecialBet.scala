package at.idot.scimplbetter.model

import javax.persistence._

@Embeddable
@scala.SerialVersionUID(uid=3445851L)
@scala.serializable
class SpecialBet {
	
	@ManyToOne
	var topscorer: Player = _
	
	@ManyToOne
	var mvp: Player =  _//best player by some jury
	
    @ManyToOne
    var winningTeam: Team = _
    
    //hibernate needs this in case all other properties are null
    var hibernateDummy = "a" 
    
    @ManyToOne
    var semifinal1: Team = _
    
    @ManyToOne
    var semifinal2: Team = _
    
    @ManyToOne
    var semifinal3: Team = _
    
    @ManyToOne
    var semifinal4: Team = _
    
    var semifinalPoints: Int = _
    
    
    def different(other: SpecialBet): Boolean = {
		topscorer != other.topscorer && 
		mvp != other.mvp && 
		winningTeam != other.winningTeam  && 
		semifinal1 != other.semifinal1 && 
		semifinal2 != other.semifinal2 && 
		semifinal3 != other.semifinal3 && 
		semifinal4 != other.semifinal4 
	
	}
    
}