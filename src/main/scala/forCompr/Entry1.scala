package forCompr
import cats.Monad

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
  }
}
