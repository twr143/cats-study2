package semigroupk
import cats._
import cats.effect._
import com.typesafe.scalalogging.StrictLogging

/**
  * Created by Ilya Volynin on 30.03.2020 at 16:44.
  */
object FoldableApp extends IOApp with StrictLogging {
//  println(IO(true).fold
  def run(args: List[String]): IO[ExitCode] = {
    IO(ExitCode.Success)
  }
}
