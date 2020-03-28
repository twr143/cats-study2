package repeat
import cats.effect.{ContextShift, ExitCode, Timer}
import monix.eval.Task
import monix.execution.Scheduler
import repeat.RepeatCommon.{Bank, Counter}

/**
  * Created by Ilya Volynin on 19.03.2020 at 11:07.
  */
object TransferMonixApp extends App with RepeatCommon {
  implicit val sche = Scheduler.forkJoin(5, 10, "transfer-scheduler")
  implicit val timer: Timer[Task] = Task.timer(sche)
  implicit val effect = Task.catsEffect
  Task
    .parZip3(firstTransfer[Task](Bank.transfer), secondTransfer[Task](Bank.transfer), thirdTransfer[Task](Bank.transfer))
    .map(_ => {
      println("all transfers completed...");
      println(s"accA...${Bank.accA}, accB... ${Bank.accB}");
      ExitCode.Success
    })
    .runSyncUnsafe()
}
