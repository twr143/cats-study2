package cats.effects
import cats.effect.IO

/**
  * Created by Ilya Volynin on 29.11.2019 at 17:22.
  */
object Apply extends App {

def putStrlLn(value: String) = IO(println(value))
val readLn = IO(scala.io.StdIn.readLine)

  (for {
    _ <- putStrlLn("What's your name?")
    n <- readLn
    _ <- putStrlLn(s"Hello, $n!")
  } yield ()).unsafeRunAsync(_ => ())//123

}
