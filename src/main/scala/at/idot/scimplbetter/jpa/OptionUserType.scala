package at.idot.jpa

import scala.{Array => A}
import org.hibernate._
import org.hibernate.usertype._
import org.hibernate.`type`._
import java.sql._
import java.io._

class StringOptionUserType extends OptionUserType { def nullableType = Hibernate.STRING }

class IntOptionUserType extends OptionUserType { 
    def nullableType = Hibernate.INTEGER 
    
    override def nullSafeGet(resultSet: ResultSet, names: A[String], owner: Object) = {
        val x = nullableType.nullSafeGet(resultSet, names(0)).asInstanceOf[Integer]
        if (x == null) None else Some(x.intValue)
    }

    override def nullSafeSet(preparedStatement: PreparedStatement, value: Object, index: Int) =
        nullableType.nullSafeSet(
            preparedStatement, 
            value.asInstanceOf[Option[Int]].map(new Integer(_)).getOrElse(null), 
            index)
}

abstract class OptionUserType extends UserType {

    def nullableType: NullableType

    def returnedClass = classOf[Option[_]]
    

    def sqlTypes = A(nullableType.sqlType)

    def nullSafeGet(resultSet: ResultSet, names: A[String], owner: Object) = {
        val x = nullableType.nullSafeGet(resultSet, names(0))
        if (x == null) None else Some(x)
    }

    def nullSafeSet(preparedStatement: PreparedStatement, value: Object, index: Int) =
        nullableType.nullSafeSet(preparedStatement, value.asInstanceOf[Option[_]].getOrElse(null), index)

    def isMutable = false

    def equals(x: Object, y: Object) = x.equals(y)

    def hashCode(x: Object) = x.hashCode

    def deepCopy(value: Object) = value

    def replace(original: Object, target: Object, owner: Object) = original

    def disassemble(value: Object) = value.asInstanceOf[Serializable]

    def assemble(cached: Serializable, owner: Object) = cached
}
