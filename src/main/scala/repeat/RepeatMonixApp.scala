package repeat
import cats.effect.{ContextShift, ExitCode, Timer}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import repeat.RepeatCommon._

/**
 * Created by Ilya Volynin on 18.03.2020 at 15:41.
 */
object RepeatMonixApp extends App with RepeatCommon {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect
  Task.parZip2(firstTask[Task](Counter.get),secondTask[Task](Counter.get) ).map(_ => {
        println("now both completed...");
        ExitCode.Success
      }).runSyncUnsafe()

}
