package cats.resource
import cats.implicits._
import caching.model.{Cat, Dog}
import cats.effect.{ContextShift, ExitCase, Resource, Timer}
import com.typesafe.scalalogging.StrictLogging
import monix.eval.Task
import scalacache.Mode
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._
import scala.util.control.NonFatal
import cats.effect.ExitCase.Completed

/**
  * Created by Ilya Volynin on 22.06.2020 at 12:24.
  */
object ResExceptionE extends App with StrictLogging {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect
  case class A(id: Long = 0)
//
  def res =
    Resource.make(Task { logger.info("aquire") } /*>> Task.raiseError[Unit](new Exception("BOOM!")).start*/ )(_ =>
      Task { logger.info("relelase begun") } >> Task.sleep(500 millis) >> Task { logger.info("relelase completed") }
    )
  def res2 =
    Resource.make(Task { logger.info("aquire 2") } /*>> Task.raiseError[Unit](new Exception("BOOM 2!")).start*/ )(_ =>
      Task { logger.info("relelase 2 begun") } >> Task.sleep(500 millis) >> Task { logger.info("relelase 2 completed") }
    )

  (for {
    r1 <- res
    r2 <- res2
  } yield (r1, r2)).use {
    case (r1, r2) =>
      Task { logger.info("res use {} {}" /*, r1.join, r2.join*/, r1, r2) } >> Task.raiseError[Unit](new Exception("BOOM 2!")) >> Task { logger.info("done") }
  } runSyncUnsafe ()

}
