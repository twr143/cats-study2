package integration.kafka.serOn.msgpack

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import integration.kafka.serOn.msgpack.Model.ColorMsg.ColorMsg
import upickle.default.{ReadWriter => RW, macroRW}

/**
  * Created by Ilya Volynin on 21.03.2020 at 13:51.
  */
object Model {
  case class AddressMsg(no: Int, street: String)
  sealed trait HumanMsg {
    def address: AddressMsg
  }
  case class PersonMsg(name: String, address: AddressMsg) extends HumanMsg
  case class PersonMsg5(name: String, address: AddressMsg, sex: Boolean, color: ColorMsg, marriageDate: Option[MyLocalDateMsg]) extends HumanMsg
  object ColorMsg extends Enumeration {
    type ColorMsg = Value
    val Red = Value(1)
    val Green = Value(2)
    val Blue = Value(3)
  }
  case class MyLocalDateMsg(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    override def toString: String = formatter.format(date)
  }
  import upickle.default._
}
