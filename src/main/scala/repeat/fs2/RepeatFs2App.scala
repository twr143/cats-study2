package repeat.fs2

import com.typesafe.scalalogging.StrictLogging
import cats._, implicits._
import cats.effect._
import fs2._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Created by Ilya Volynin on 18.03.2020 at 10:02.
 */
object RepeatFs2App extends IOApp with StrictLogging {

  def run(args: List[String]): IO[ExitCode] = {
    val myStream = (Stream.emit("Basic setup...").covary[IO].showLinesStdOut
      ++ Stream.emit("Rest of the app...").covary[IO].showLinesStdOut
      ++ Stream.sleep(2 seconds))
      .concurrently(Stream.awakeEvery[IO](500 millis).flatMap(_ => Stream.emit("foo").covary[IO].showLinesStdOut))
    myStream.compile.drain.as(ExitCode.Success)
  }
}
