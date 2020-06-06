package withdrawals
import cats.Show
import cats.data.Tuple2K
import cats.effect.{ContextShift, ExitCode, Timer}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import fs2._

import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 06.06.2020 at 15:29.
  */
object EntryWidthdrawals extends App with Repository {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect
  val size = 10
  implicit val showResultRaws = new Show[(Long, List[Withdrawal])] {

    def show(tuple: (Long, List[Withdrawal])): String = s"wd(${tuple._1}, ${tuple._2})"
  }

  val s = Stream
    .emit(List[Char]('a', 'b', 'c', 'd'))
    .repeat
    .flatMap(l => Stream.chunk(Chunk.seq(l)))
    .metered[Task](600.millis)
    .take(size)
    .covary[Task]
    .evalScan((0L, List.empty[Withdrawal])) {
      case ((lastReadId, _), _) =>
        findAllWaitingOrderedBySerialIdAsc(1, lastReadId)
    }
    .showLinesStdOut
  s.compile.drain.map(_ => ExitCode.Success).runSyncUnsafe()

}
