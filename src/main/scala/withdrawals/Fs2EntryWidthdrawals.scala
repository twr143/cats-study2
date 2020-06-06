package withdrawals
import cats.Show
import cats.data.Tuple2K
import cats.effect.{ContextShift, ExitCode, Timer}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import fs2._

import scala.collection.mutable
import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 06.06.2020 at 15:29.
  */
object Fs2EntryWidthdrawals extends App with Repository {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect
  val size = 7
  implicit val showResultRaws = new Show[(Long, List[Withdrawal])] {

    def show(tuple: (Long, List[Withdrawal])): String = s"wd(${tuple._1}, ${tuple._2})"
  }

  implicit val showOnlyWd = new Show[Withdrawal] {

    def show(wd: Withdrawal): String = s"w:id: ${wd.id},gr-id: ${wd.groupId},info: ${wd.info}"
  }

  implicit val showMapWithdrawalsl = new Show[mutable.Map[Int, Set[Withdrawal]]] {

    def show(m: mutable.Map[Int, Set[Withdrawal]]) =
      "mapwd = \n" + m.keySet.foldLeft("") { case (str, k) => str + k + "->" + m(k).toString() + "\n" }
  }

  def updateValue[A, B, C <: mutable.Map[A, B]](m: C, k: A, DefaultValue: B)(f: B => B): Unit = m.update(k, f(m.getOrElse(k, DefaultValue)))
  def processWithdrawalGroup(w: Set[Withdrawal]): Task[Unit] = {
    Task.sleep(300 millis) >> Task(())
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
        findAllWaitingOrderedBySerialIdAsc(2, lastReadId)
    }
    .groupWithin(5, 1000 millis)
    .map {
      case chunk =>
        val m = mutable.Map.empty[Int, Set[Withdrawal]]
        chunk.foreach { case (amt, lst) => lst.foreach(w => updateValue(m, w.groupId, Set.empty[Withdrawal])(_ + w)) }; m
    }
    .mapAsyncUnordered(8) { m =>
      Task
        .parZip3(
          processWithdrawalGroup(m.getOrElse(1, Set.empty[Withdrawal])),
          processWithdrawalGroup(m.getOrElse(2, Set.empty[Withdrawal])),
          processWithdrawalGroup(m.getOrElse(3, Set.empty[Withdrawal]))
        )
        .map(_ => m)
    }
    .showLinesStdOut
  s.compile.drain.map(_ => ExitCode.Success).runSyncUnsafe()
  //grouping by criteria into streams isn't possible :(
}
