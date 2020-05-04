import org.scalatest.{FlatSpec, Matchers}
import cats.effect.IO
import cats.data._
import cats.implicits._
import userRegDyn.UserValidator._
import cats.data.Validated._

class UserValidationTest extends FlatSpec with Matchers {
  it should "pass valid user object" in {
    validate("ilya", "hjj", "8") shouldBe Valid("")
  }
  it should "fail if pass too short" in {
    validate("ilya", "hj", "8") shouldBe Invalid(Chain(shortPass))
  }
  it should "fail if pass too short and contains invalid chars" in {
    validate("ilya", "h0", "8") shouldBe Invalid(Chain(validPass, shortPass))
  }

}
