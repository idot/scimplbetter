package at.idot.scimplbetter.model

import org.jasypt.util.password.StrongPasswordEncryptor
import javax.persistence._


@Entity
@Table( name = "users" )
@scala.SerialVersionUID(uid=784526183L)
@scala.serializable
class User {
	
	@Id @GeneratedValue
	var id: Int = _
	
	@Version
	var version :Int = _
	
    var firstName :String = _
	var lastName :String = _
	
	@Column(nullable=false, length=20, unique=true)
	var userName :String = _
	
	@Column(nullable=false)
	var email: String = _
	
	@Column(nullable=false)
	var password_hash: String = _
	
	
	
	def password: String = this.password_hash

	@Column(nullable=false,length=45)
	def password_=(in: String) = this.password_hash = encrypt(in)
	
	def authenticate(in: String): Boolean = new StrongPasswordEncryptor().checkPassword(in, password) 

    private def encrypt(in: String): String = new StrongPasswordEncryptor().encryptPassword(in)

	@OneToMany(mappedBy="user",cascade=Array(CascadeType.ALL))
	var bets :java.util.Set[Bet] = new java.util.HashSet[Bet]
	
	@Embedded
	var specialBet :SpecialBet = _
		
	def betsForGame(game: Game): Seq[Bet] = {
		import scala.collection.JavaConversions._
	    bets.filter(_.game == game).toList
	}
	
	/***
	* not used atm
	*/
	var stylesheet: String = _
	
	/***
	*send our emails immediately after game is closed
	*/
	var emailCheck: Boolean = _
	
	/***
	* if user had instructions (currently: submitted the userDetails form once, unless admin)
	* if false login -> userDetails
	*/
	var hadInstructions: Boolean = _
	
	/***
	* if user is active
	* not used atm
	*/
	var isActive: Boolean = _
	
	/***
	* if user can bet (currently: == hadInstructions &! admin)
	*/
	var canBet: Boolean = _
	
	/***
	* admins can register other users but cant bet
	**/
	var isAdmin: Boolean = _
	
	/***
	* total points of user
	*/
	var points: Int = _
	
	/***
	* number of invalid login attemtps for this user
	*/
	var invalidLogins: Int = _
	
	@Column(nullable=false)
	var institute: String = _
	
	var gravatarEmail: String = _
	
	/***
	 * see User.gravatarDefault
	 */
	var gravatarDefault: String = _
	
	/***
	* userId of registering admin
	* not user out of laziness
	*/	
	var registeredBy: String = _
	
	override def hashCode: Int = {
		userName.hashCode
	 }

	override def equals(that: Any): Boolean = {
		that match {
			case null => false
			case that: AnyRef if this eq that => true
			case user: User => user.userName == userName
			case _ => false
		}
	}
}


object User {
	
	def apply(userName: String, password: String, email: String): User = {
		val user = new User
		user.userName = userName
		user.password = password
		user.email = email
		user.firstName = ""
		user.lastName  = ""
		user.institute = ""
		user.registeredBy = ""
		user.gravatarEmail = ""
		user.isActive = true
		user.invalidLogins = 0
		user.gravatarDefault = "monsterid"
		user.specialBet = new SpecialBet
		user
	}
	
	
	val gravatarDefault = List("identicon", "monsterid", "wavatar", "none")
	
}

