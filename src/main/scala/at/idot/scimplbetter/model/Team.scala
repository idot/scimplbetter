package at.idot.scimplbetter.model

import javax.persistence._

@Entity
@Table( name = "teams" )
@scala.SerialVersionUID(uid=91337591L)
@scala.serializable
case class Team {
	
		@Id @GeneratedValue
		var id: Int = _
		
		@Version
		var version: Int = _
		
		var name: String = _
		
		@Embedded
		var country: Country = _
	
		@OneToMany(mappedBy="team")
		var players: java.util.Set[Player] = new java.util.HashSet[Player]
		
		override def hashCode: Int = {
			name.hashCode * 3
	 	}

		override def equals(that: Any): Boolean = {
			that match {
				case null => false
				case that: AnyRef if this eq that => true
				case team: Team => team.name == name
				case _ => false
			}
		}
		
		override def toString() = name

}

