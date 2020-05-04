import org.scalatest.{FlatSpec, Matchers}
import kleisli.Common
import cats.effect.IO
import cats.data._
import cats.implicits._
import cats.arrow._
class KleisliTest extends FlatSpec with Matchers with kleisli.Common{

  it should "verify kleisli chain is valid" in {
    // when
    val io = Kleisli(generate) andThen Kleisli(process) andThen Kleisli(save) andThen Kleisli(confirm) apply ()  
    io.unsafeRunSync() shouldBe true
    // then
  }
  it should "chain kleisli propperly" in {

      val k1 = Kleisli[IO, Int, Int] {
        i => IO(i * 2)
      }

      val k2 = Kleisli[IO, Int, Int] {
        j => IO(j + 1)
      }
      val r2 = (k1 |+| k2)(1)
      r2.unsafeRunSync() shouldBe 4
  }
    it should "arithmetics" in {

      2+3 shouldBe 5
  }

} 