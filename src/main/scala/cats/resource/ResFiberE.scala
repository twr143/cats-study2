package cats.resource
import cats.effect._
import com.typesafe.scalalogging.StrictLogging
import monix.eval.{Fiber, Task}
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 22.06.2020 at 12:24.
  */
object ResFiberE extends StrictLogging {
  def main(args: Array[String]): Unit = {

    implicit val cs: ContextShift[Task] = Task.contextShift(global)

    implicit val timer: Timer[Task] = Task.timer(global)
    implicit val effect = Task.catsEffect
    case class A(id: Long = 0)
//
    def res =
      Resource.make((Task { logger.info("aquire"); 0 } /*>> Task.raiseError[Int](new Exception("BOOM!"))*/ ).start)(_ =>
        Task { logger.info("relelase begun") } >> Task.sleep(500 millis) >> Task { logger.info("relelase completed") }
      )
    def res2 =
      Resource.make(Task { logger.info("aquire 2"); 1 }.start /*>> Task.raiseError[Unit](new Exception("BOOM 2!")).start*/ )(_ =>
        Task { logger.info("relelase 2 begun") } >> Task.sleep(500 millis) >> Task { logger.info("relelase 2 completed") }
      )
    (for {
      r <- res
      r2 <- res2
    } yield (r, r2))
      .use {

//  (res, res2).mapN((_, _)).use {
        case (r, r2) =>
          (for {
            j <- r.join
            j2 <- r2.join
          } yield (j, j2)).flatMap {
            case (j, j2) =>
              Task { logger.info("res use {} {}", j, j2) } >> Task.raiseError[Unit](new Exception("BOOM 2!")) >> Task { logger.info("done") }
          }
      } runSyncUnsafe ()

  }
}
