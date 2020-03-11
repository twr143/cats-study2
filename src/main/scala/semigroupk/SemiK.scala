package semigroupk
import cats.data.Kleisli
import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import cats.implicits._

/**
 * Created by Ilya Volynin on 11.03.2020 at 12:25.
 */
object SemiK extends App with StrictLogging {

  val r = 1 |+| 2

  logger.info("result {}", r)

  val k1 = Kleisli[IO, Int, Int] {
    i => IO(i * 2)
  }

  val k2 = Kleisli[IO, Int, Int] {
    j => IO(j + 1)
  }
  val r2 = (k1 |+| k2)(1)
  logger.info("kleisli |+| result {}", r2.unsafeRunSync())

}
