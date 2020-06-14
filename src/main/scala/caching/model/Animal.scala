package caching.model

/**
  * Created by Ilya Volynin on 14.06.2020 at 18:47.
  */
sealed trait Animal
final case class Cat(id: Int, name: String, colour: String) extends Animal
final case class Dog(id: Int, name: String, colour: String) extends Animal
