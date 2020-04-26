package userRegDyn
import scala.util.Try
import cats.implicits._

/**
  * Created by Ilya Volynin on 26.04.2020 at 13:02.
  */
object UserRegDynE1 extends App {
  val rules = List(
    Rule("age", true, a => {
      Try(a.toInt).fold(_ => s"age is NAN", aa => if (aa < 0) "age should be nonnegative" else "")
    }),
    Rule("age", true, a => {
      Try(a.toInt).fold(_ => s"age is NAN", aa => if (aa > 9) "age should be less than 10" else "")
    }),
    Rule("userName", true, v => {
      if (v.matches("^[a-zA-Z0-9]+$")) "" else "illegal characters in username"
    }),
    Rule("userName", true, v => {
      if (v.length > 2) "" else "username should contain 3 or more characters"
    }),
    Rule("passWord", true, p => {
      if (p.matches("^([a-z]).*$"))
        ""
      else "illegal characters in password"
    }),
    Rule("passWord", true, p => {
      if (p.length > 2)
        ""
      else "password should contain 3 or more characters"
    })
  )
  val params = Map("age" -> "15", "userName" -> "il", "passWord" -> "@#1234$@adf")
  val params2 = Map("age" -> "9", "userName" -> "ilya", "passWord" -> "abc")

  println(rules.foldLeft("".validNec[String])((chain, rule) => chain.combine(FieldValidatorNec.validateField(params, rule))))
  println(rules.foldLeft("".validNec[String])((chain, rule) => chain.combine(FieldValidatorNec.validateField(params2, rule))))

}
