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
    (t >> Effect[F].pure {
      j += 1
    } >> Timer[F].sleep(200 millis))
      .handleError { e =>
        logger.error(errorMsg, e)
      }
      .iterateWhile(_ => j <= 6)
  }

  protected def firstTask[F[_] : Effect : Timer](counter: => Int): F[Unit] =
    runPeriodically[F, Unit]("no err")(Effect[F].delay {
      println(s"i=$counter")
    })

  protected def secondTask[F[_] : Effect : Timer](counter: => Int): F[Unit] =
    runPeriodically[F, Unit]("no err")(Effect[F].delay {
      println(s"i=${counter + 10}")
    })
}

object RepeatCommon {

  case object Counter {
    var i=0
    def get :Int = {
      i+=1
      i-1
    }
  }

}
