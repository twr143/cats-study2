package cats.resource
import caching.model.{Cat, Dog}
import cats.effect.{ContextShift, Resource, Timer}
import com.typesafe.scalalogging.StrictLogging
import monix.eval.Task
import scalacache.Mode
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 22.06.2020 at 12:24.
  */
object ResE1 extends App with StrictLogging {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect

  lazy val res = Resource.make(Task { logger.info("aquire") }
    >> Task.sleep(500 millis))(_ =>
    Task { logger.info("relelase begun") }
      >> Task.sleep(500 millis) >> Task { logger.info("relelase completed") }
  )

  res.use(_ => Task { logger.info("res use") }) >> Task { logger.info("done") } runSyncUnsafe ()

}
