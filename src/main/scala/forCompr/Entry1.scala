package forCompr
import cats.Monad
import fs2._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._
import cats.implicits._

/**
  * Created by Ilya Volynin on 04.01.2020 at 13:19.
  */
object Entry1 {

  abstract class R[F[_] <: Monad[F]] {

    def f[A](x: A): F[A]
    def cond[A](x: A): Boolean

    def g[A, B, C](x: A, y: B): F[C]

//        def foo1[A](a: F[A]) =
//          for {
//            unpA: A <- a
//            b: A <- f(unpA) if cond(unpA)
//          } yield g(unpA, b)
//    def foo2[A, B, C](a: F[A]):F[C] =
//      a.flatMap {
//        case unpA: A => f(unpA).flatMap[A, C] {
//          case b: A => g(unpA, b)
//          case _ => _
//        }
//        case _ => _
//      }

    //          for {
    //            unpA: A <- a
    //            b: A <- f(unpA)
    //          } yield g(unpA, b)
  }

  def main(args: Array[String]): Unit = {
    /*    val res = Stream
      .range(1, 4, 1)
      .map(i => List(i * 5, i * 5 - 1, i * 5 - 2, i * 5 - 3, i * 5 - 4))
      .flatMap(lst => Stream.chunk(Chunk.seq(lst)))
      .fold("")(_ + " " + _)
      .compile
      .last
      .getOrElse("")
    println(s"res=$res")*/

    Stream
      .range(1, 6, 1)
      .evalMap(i => Task.now(i))
      .takeWhile(_ < 3)
      .fold(0)(_ + _)
      .compile
      .last
      .map(_.getOrElse(0))
      .map(a => { println(a); a })
      .runSyncUnsafe()

  }
}
