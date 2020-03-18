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
    runForeverPeriodically("no err")(IO {
      println(s"i=$i");
      i += 1
    }).flatMap(_.join).map(_ => ExitCode.Success)
  }

  private def runForeverPeriodically[T](errorMsg: String)(t: IO[T]): IO[Fiber[IO, Option[Unit]]] = {
    var j = 0
    (t >> IO {
      j += 1
    } >> IO.sleep(500 millis))
      .handleError { e =>
        logger.error(errorMsg, e)
      }
      .untilM[Option](IO(j > 5))
      .start
  }
}
