package cats.applicatives
import cats.implicits._
//import cats.syntax.traverse._
//import cats.instances.list._
//import cats.instances.option._

/**
  * Created by Ilya Volynin on 11.12.2019 at 9:13.
  */
object ApplicE1 {

  def getUserByIdOption: Int => Option[Int] = {
    i => if (i == 2) None else Some(i)
  }

  def main(args: Array[String]): Unit = {
//    val result = List(1, 2, 3).traverse(getUserByIdOption)
//    println(result)
    val a: Option[Int] = None
    println((Some(1), a, Some(3)).mapN((a, b, c) => c))
  }
}
