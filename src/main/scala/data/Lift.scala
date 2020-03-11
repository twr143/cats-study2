package data
import cats.data.{EitherT, OptionT}
import com.typesafe.scalalogging.StrictLogging

/**
 * Created by Ilya Volynin on 03.03.2020 at 15:18.
 */
object Lift extends App with StrictLogging {
  import cats.data.EitherT
  import cats.implicits._

  val o: Option[Int] = Some(3)

  val n: Option[Int] = None

  println(EitherT.liftF(o))

  //    cats.data.EitherT[Option,Nothing,Int] = EitherT(Some(Right(3)))
  println(EitherT.liftF(n))

  //    cats.data.EitherT[Option,Nothing,Int] = EitherT(None)
  val l = List(1, 2, 3)
   val optLifted = OptionT.liftF(l)
  println(optLifted)
}
