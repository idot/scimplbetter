package at.idot.scimplbetter.model

import org.hibernate.annotations.Type
import javax.persistence._
import java.util.Calendar


/**
 * Stores one time codes with creation date
 * e.g. for password change request
 *
 */
@Entity
@scala.SerialVersionUID(uid=73547618L)
@scala.serializable
class OneTimeCode {

	@Id @GeneratedValue
	var id: Int = _
	
	@Column(nullable=false, length=40, unique=true)
	var code: String = _
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	var creationDate: Calendar = _
	
	var description: String = _
	
}