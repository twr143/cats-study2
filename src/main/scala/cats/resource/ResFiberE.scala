package cats.resource
import cats.effect.Resource
import com.typesafe.scalalogging.StrictLogging
import monix.eval.{Fiber, Task}

import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._
import cats.implicits._

/**
  * Created by Ilya Volynin on 22.06.2020 at 12:24.
  */
object ResFiberE extends StrictLogging {

  def main(args: Array[String]): Unit = {
    case class A(id: Long = 0)
    //
    lazy val res =
      Resource.make((Task {
        logger.info("aquire"); 0
      } >> Task.raiseError[Int](new Exception("Res BOOM!"))).start)(_ =>
        Task {
          logger.info("relelase begun")
        } >> Task.sleep(500 millis) >> Task {
          logger.info("relelase completed")
        }
      )
    lazy val res2 =
      Resource.make(Task {
        logger.info("aquire 2"); 1
      }.start /*>> Task.raiseError[Unit](new Exception("BOOM 2!")).start*/ )(_ =>
        Task {
          logger.info("relelase 2 begun")
        } >> Task.sleep(500 millis) >> Task {
          logger.info("relelase 2 completed")
        }
      )
    (for {
      r <- res
      r2 <- res2
    } yield (r, r2))
      .use {
        //  (res, res2).mapN((_, _)).use {
        case (r, r2) =>
          for {
            j2 <- r2.join
            j3 <- r2.join
            _ <- Task {
              logger.info("res2 double use {} {}", j2, j3)
            }
            j <- r.join
            _ <- Task.raiseError[Unit](new Exception("BOOM 2!"))
            _ <- Task {
              logger.info("done")
            }
          } yield ()
      } runSyncUnsafe ()
  }
}
