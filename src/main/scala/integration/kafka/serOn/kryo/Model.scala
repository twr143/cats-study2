package integration.kafka.serOn.kryo

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import integration.kafka.serOn.kryo.Model.Color.Color

/**
  * Created by Ilya Volynin on 21.03.2020 at 13:51.
  */
object Model {
  case class Address(no: Int, street: String)
  sealed trait Human
  case class Person(name: String, address: Address) extends Human
  case class Person2(name: String, address: Address, sex: Boolean) extends Human
  case class Person3(name: String, address: Address, sex: Boolean, color: Color) extends Human
  case class Person4(name: String, address: Address, sex: Boolean, color: Color, marriageDate: Option[LocalDate]) extends Human
  case class Person5(name: String, address: Address, sex: Boolean, color: Color, marriageDate: Option[MyLocalDate]) extends Human
  object Color extends Enumeration {
    type Color = Value
    val Red = Value(1)
    val Green = Value(2)
    val Blue = Value(3)
  }
  case class MyLocalDate(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    override def toString: String = formatter.format(date)
  }
}
