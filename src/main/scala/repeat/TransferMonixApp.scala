package repeat
import cats.effect.{ContextShift, ExitCode, Timer}
import monix.eval.Task
import repeat.RepeatCommon.{Bank, Counter}
import repeat.RepeatMonixApp.{firstTask, secondTask}
import monix.execution.Scheduler.Implicits.global

/**
 * Created by Ilya Volynin on 19.03.2020 at 11:07.
 */
object TransferMonixApp extends App with RepeatCommon {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect
  Task.parZip3(firstTransfer[Task](Bank.transfer),secondTransfer[Task](Bank.transfer),
    thirdTransfer[Task](Bank.transfer) ).map(_ => {
        println("all transfers completed...");
        println(s"accA...${Bank.accA}, accB... ${Bank.accB}");
        ExitCode.Success
      }).runSyncUnsafe()
}
