package repeat
import cats.effect.{Effect, Timer}
import cats.effect._
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by Ilya Volynin on 18.03.2020 at 14:53.
  */
trait RepeatCommon extends StrictLogging {

  protected def runPeriodically[F[_]: Effect: Timer, T](errorMsg: String, times: Int, dur: FiniteDuration = 100 millis)(t: F[T]): F[Unit] = {
    var j = 0
    (t >> Effect[F].delay { j += 1 } >> Timer[F].sleep(dur))
      .handleError(e => logger.error(errorMsg, e))
      .iterateWhile(_ => j < times)
  }

  protected def firstTask[F[_]: Effect: Timer](counter: => Long): F[Unit] =
    runPeriodically[F, Unit]("no err", 7)(Effect[F].delay { logger.info(s"i=$counter") })
  protected def secondTask[F[_]: Effect: Timer](counter: => Long): F[Unit] =
    runPeriodically[F, Unit]("no err", 7)(Effect[F].delay { logger.info(s"i=${counter + 100}") })

  protected def firstTransfer[F[_]: Effect: Timer](transfer: => Int): F[Unit] =
    runPeriodically[F, Unit]("no err", 1, 50 millis)(Effect[F].delay { logger.info(s"1 funds transferred=$transfer") })

  protected def secondTransfer[F[_]: Effect: Timer](transfer: => Int): F[Unit] =
    runPeriodically[F, Unit]("no err", 400, 1 millis)(Effect[F].delay { logger.info(s"2 funds transferred=$transfer") })

  protected def thirdTransfer[F[_]: Effect: Timer](transfer: => Int): F[Unit] =
    runPeriodically[F, Unit]("no err", 520, 1 millis)(Effect[F].delay {
      logger.info(s"3 funds transferred=$transfer")
    })
}

object RepeatCommon {
  import java.util.concurrent.atomic.AtomicLong

  case object Counter {

    var i = new AtomicLong(0)

    def get: Long = i.incrementAndGet()
  }

  case object Bank {

    var accA: Int = 100
    var accB: Int = 0
    val r = new Random()

    def transfer: Int = synchronized[Int] {
      val amount = r.nextInt(100)
      if (accA >= amount) {
        accA -= amount;
        accB += amount
        amount
      } else if (accB >= amount) {
        accA += amount;
        accB -= amount
        amount
      } else 0
    }
  }

}
