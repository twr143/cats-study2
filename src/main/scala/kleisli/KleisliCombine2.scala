package kleisli
import cats._
import cats.data._
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
/**
  * Created by Ilya Volynin on 01.03.2020 at 18:32.
  */
object KleisliCombine2 extends App with LazyLogging with Common {

  val kleisliCombine_2 = Kleisli(generate) andThen Kleisli(process) andThen Kleisli(save)
}
