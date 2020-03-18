package repeat
import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import cats.implicits._
import scala.concurrent.duration._

/**
 * Created by Ilya Volynin on 18.03.2020 at 10:46.
 */
object RepeatIOApp extends IOApp with RepeatCommon {
   import repeat.RepeatCommon._
  def run(args: List[String]): IO[ExitCode] = {
    par2(firstTask[IO](Counter.get),secondTask[IO](Counter.get) ).map(_ => {
      println("now both completed...");
      ExitCode.Success
    })
  }

  def par2[A, B](ioa: IO[A], iob: IO[B]): IO[(A, B)] =
    (ioa.start, iob.start).tupled.bracket { case (fa, fb) =>
      (fa.join, fb.join).tupled
    } { case (fa, fb) => fa.cancel >> fb.cancel }

}
