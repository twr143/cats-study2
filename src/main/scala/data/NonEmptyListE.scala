package data
import cats.arrow.FunctionK
import cats.data.{NonEmptyList, OptionT}
import com.typesafe.scalalogging.StrictLogging
import cats.implicits._

/**
 * Created by Ilya Volynin on 03.03.2020 at 20:05.
 */
object NonEmptyListE extends App with StrictLogging {

  val a = NonEmptyList.of[Option[Int]](Some(1), Some(2), Some(3))

  val k = a.foldLeft(Option(List.empty[Int])) { case (l, o) => if (o.nonEmpty) l.map(  _.:+(o.get)) else None }
  println(NonEmptyList.fromList(k.getOrElse(List.empty[Int])))
}