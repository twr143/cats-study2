package repeat
import cats.effect.{Effect, Timer}
import cats.effect._
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.duration._

/**
 * Created by Ilya Volynin on 18.03.2020 at 14:53.
 */
trait RepeatCommon extends StrictLogging {

  protected def runPeriodically[F[_] : Effect : Timer, T](errorMsg: String)(t: F[T]): F[Unit] = {
    var j = 0
    (t >> Effect[F].delay {
      j += 1
    } >> Timer[F].sleep(100 millis))
      .handleError { e =>
        logger.error(errorMsg, e)
      }
      .iterateWhile(_ => j <= 6)
  }

  protected def firstTask[F[_] : Effect : Timer](counter: => Long): F[Unit] =
    runPeriodically[F, Unit]("no err")(Effect[F].delay {
      logger.info(s"i=$counter")
    })

  protected def secondTask[F[_] : Effect : Timer](counter: => Long): F[Unit] =
    runPeriodically[F, Unit]("no err")(Effect[F].delay {
      logger.info(s"i=${counter + 100}")
    })
}

object RepeatCommon {
  import java.util.concurrent.atomic.AtomicLong
  case object Counter {

    var i = new AtomicLong(0)

    def get: Long = i.incrementAndGet()
  }
}
