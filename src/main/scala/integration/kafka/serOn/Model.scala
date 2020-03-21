package integration.kafka.serOn

/**
  * Created by Ilya Volynin on 21.03.2020 at 13:51.
  */
object Model {
  case class Address(no: Int, street: String)
  case class Person(name: String, address: Address)

}
