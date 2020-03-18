package repeat
import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import cats.implicits._
import scala.concurrent.duration._

/**
 * Created by Ilya Volynin on 18.03.2020 at 10:46.
 */
object RepeatIOApp extends IOApp with StrictLogging {

  def run(args: List[String]): IO[ExitCode] = {
    var i = 0;
    par2(runPeriodically("no err")(IO {
      println(s"i=$i");
      i += 1
    }),runPeriodically("no err")(IO {
          println(s"i+10=${i+10}");
        })).map(_ => {println("now both completed...");ExitCode.Success})
  }
  def par2[A, B](ioa: IO[A], iob: IO[B]): IO[(A, B)] =
    (ioa.start, iob.start).tupled.bracket { case (fa, fb) =>
      (fa.join, fb.join).tupled
    } { case (fa, fb) => fa.cancel >> fb.cancel }

  private def runPeriodically[T](errorMsg: String)(t: IO[T]): IO[Option[Unit]] = {
    var j = 0
    (t >> IO {
      j += 1
    } >> IO.sleep(200 millis))
      .handleError { e =>
        logger.error(errorMsg, e)
      }
      .untilM[Option](IO(j > 9))
  }
}
