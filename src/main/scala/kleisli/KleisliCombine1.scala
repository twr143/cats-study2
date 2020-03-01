package kleisli
import cats.data.Kleisli
import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Ilya Volynin on 01.03.2020 at 18:28.
  */
object KleisliCombine1 extends App with LazyLogging with Common {
  val kleisliCombine_1: Kleisli[IO, Unit, Boolean] = {
     val generateK:Kleisli[IO, Unit, Int] = Kleisli(generate)
     val processK:Kleisli[IO, Int, Double] = Kleisli(process)
     val saveK:Kleisli[IO, Double, Boolean] = Kleisli(save)
     generateK andThen processK andThen saveK
   }
  logger.info(s"Kleilis example 1: ${kleisliCombine_1.run().unsafeRunSync()}")
}
