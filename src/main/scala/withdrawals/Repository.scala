package withdrawals
import monix.eval.Task
import monix.execution.Scheduler
import cats.implicits._

/**
  * Created by Ilya Volynin on 06.06.2020 at 15:16.
  */
trait Repository {
  val data = List(
    Withdrawal(10, 1, "ten doll"),
    Withdrawal(20, 2, "twent doll"),
    Withdrawal(30, 3, "firty doll"),
    Withdrawal(40, 1, "forty doll"),
    Withdrawal(50, 2, "fifty doll"),
    Withdrawal(60, 3, "sixty doll"),
    Withdrawal(70, 1, "seventy doll"),
    Withdrawal(80, 2, "eighty doll"),
    Withdrawal(90, 3, "ninty doll"),
    Withdrawal(100, 1, "sotnya doll")
  )
  def findAllWaitingOrderedBySerialIdAsc(maxBatchSize: Int, lastReadId: Long)(implicit s: Scheduler): Task[(Long, List[Withdrawal])] = {
    for {
      resultSet <- Task.delay(data.filter(_.id > lastReadId).take(maxBatchSize))
      r <- Task.delay(resultSet.isEmpty).ifM(Task.now((lastReadId, List.empty[Withdrawal])), Task.now((resultSet.last.id, resultSet)))
    } yield r
  }

}
