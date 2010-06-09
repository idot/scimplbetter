package at.idot.scimplbetter.model

import org.hibernate.annotations.OptimisticLock
import org.hibernate.annotations.BatchSize
import java.text.SimpleDateFormat
import java.util.Calendar

import javax.persistence._

@Entity
@Table( name = "games" )
@scala.SerialVersionUID(uid=284796643L)
@scala.serializable
class Game {
	     @Transient
		 val sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm")
			
		 @Id @GeneratedValue
	     var id: Int = _
	     
	     @Version
	     var version: Int = _
	     
	     @Embedded
		 var result: Result = _
		 
		 @ManyToOne(cascade = Array(CascadeType.PERSIST))
	     var firstTeam: Team = _
	     
	     @ManyToOne(cascade = Array(CascadeType.PERSIST))
	     var secondTeam: Team = _
	     
	     @Embedded
	     var level: Level = _
	     
	     //TODO check optimistic lock exception on simultaneus ajax update from different users
         //@OptimisticLock(excluded=true) could help
	     @OneToMany(mappedBy="game", cascade=Array(CascadeType.ALL))
	     @BatchSize(size=50)
	     var bets: java.util.Set[Bet] = new java.util.HashSet[Bet]
	     
	     @Temporal(TemporalType.TIMESTAMP)
	     @Column(nullable=false)
	     var date: Calendar = _
	     var calculated: Boolean = _
	     
	     @Column(unique=true, nullable=false,insertable=true,updatable=true)
	     var nr: Int = _
	     
	     var venue: String = _
	     var gameGroup: String = _
	  
	     
	     def resultPrettyPrint = if(calculated) result.goalsTeam1+":"+result.goalsTeam2 else "NA"
	    	 
	     def datePrettyPrint = sdf.format(date.getTime)
	    	 
	     /***
	      * returns only the bets for a user
	      * squeryl does this more elegant
	      *  we should of course only have one/user/game
	      *  
	      * @param user
	      * @return
	      */
	     def betsForUser(user: User): Seq[Bet] = {
	    	 import scala.collection.JavaConversions._
	    	 bets.filter(_.user == user).toList
	     }
	     
	  /**
      * checks if betting is possible
      * 
      * @return if game closed rie ne vas plus
      */
	    def closed() :Boolean = {
	    	val set = settings
	    	val now = set.now
	    	set.closed(date, now)	
	    }
	     
	    def settings: Settings = BetterSettings
	    
	    override def hashCode: Int = {
			nr * 7
		}
	
		override def equals(that: Any): Boolean = {
			that match {
				case null => false
				case that: AnyRef if this eq that => true
				case game: Game => game.nr == nr
				case _ => false
			}
		}

}


