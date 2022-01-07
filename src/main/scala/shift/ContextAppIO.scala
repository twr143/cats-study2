package shift

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import cats.effect._

/**
  * Created by twr143 on 27.05.2021 at 9:56.
  */
object ContextAppIO {
  val printThread = IO { println(Thread.currentThread().getName) }
  val ec1 = ExecutionContext.fromExecutor(Executors.newCachedThreadPool(new NamedThreadFactory("ec1", true)))
  val ec2 = ExecutionContext.fromExecutor(Executors.newCachedThreadPool(new NamedThreadFactory("ec2", true)))
  val ec3 = Executors.newCachedThreadPool(new NamedThreadFactory("ec3", true))

  val cs1 = IO.contextShift(ec1)
  val cs2 = IO.contextShift(ec2)

  def run(name: String)(th: IO[_]): Unit = {
    println(s"-- $name --")
    th.unsafeRunSync()
    println()
  }

  def main(args: Array[String]): Unit = {
    run("Shift") {
      printThread *> IO.shift(ec1) *> printThread *> IO.shift(ec2) *> printThread
    }
  }
}
