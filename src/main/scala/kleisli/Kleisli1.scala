package kleisli
import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
/**
  * Created by Ilya Volynin on 01.03.2020 at 18:15.
  */
object Kleisli1 extends App with LazyLogging with Common {
    val forComp: Unit => Boolean = _ => {
        val comboForComp: Unit => IO[Boolean] = _ => {
          for {
            number <- generate()
            processed <- process(number)
            result <- save(processed)
          } yield result
        }
        comboForComp().unsafeRunSync()
      }
      logger.info(s"For comprehension version: ${forComp()}")
}
