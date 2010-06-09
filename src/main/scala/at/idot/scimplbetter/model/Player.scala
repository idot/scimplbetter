package at.idot.scimplbetter.model

import javax.persistence._

@Entity
@Table( name = "players" )
@scala.SerialVersionUID(uid=284796643L)
@scala.serializable
class Player {
	
	@Id @GeneratedValue
	var id: Int = _
	
	var firstName :String =  _
	var lastName :String = _
	var role :String = _
	
	@ManyToOne
	@JoinColumn(name="team_fk")
	var team: Team = _

	override def hashCode: Int = {
		firstName.hashCode * 3 + lastName.hashCode * 7 + team.hashCode * 13
	 }

	override def equals(that: Any): Boolean = {
		that match {
			case null => false
			case that: AnyRef if this eq that => true
			case player: Player => player.firstName == firstName && player.lastName == lastName && player.team == team
			case _ => false
		}
	}
	
	override def toString() = firstName + " " + lastName
}

