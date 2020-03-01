package kleisli
import cats.effect.IO

/**
  * Created by Ilya Volynin on 01.03.2020 at 18:28.
  */
trait Common {
  val r = scala.util.Random
  val generate: Unit => IO[Int] = _ => IO.pure(r.nextInt(100))
   val process: Int => IO[Double] = num => IO.pure(num * math.Pi)
   val save: Double => IO[Boolean] = number => IO.pure(true)

}
