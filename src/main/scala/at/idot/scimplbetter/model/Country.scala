package at.idot.scimplbetter.model

import javax.persistence._

@Embeddable
@scala.SerialVersionUID(uid=23123571L)
@scala.serializable
class Country {
		
	
	var countryName :String = _
	
	@Lob
	var flag :Array[Byte] = _

   

}
