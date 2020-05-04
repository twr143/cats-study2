package userRegDyn
import FieldValidatorNec._
import scala.util.Try
import cats.data._
object UserValidator {
  lazy val ageNotNAN = s"age is NAN"
  lazy val ageSBNN = "age should be nonnegative"
  lazy val ageSBl10 = "age should be less than 10"
  lazy val validUserName = "illegal characters in username"
  lazy val tooShortUserName = "username should contain 3 or more characters"
  lazy val validPass = "illegal characters in password"
  lazy val shortPass = "password should contain 3 or more characters"
  
  def validate(userName: String, password: String, age: String): ValidatedNec[String, String] = {
    val rules = List(
      Rule("age", true, a => {
        Try(a.toInt).fold(_ => ageNotNAN, aa => if (aa < 0) ageSBNN else "")
      }),
      Rule("age", true, a => {
        Try(a.toInt).fold(_ => ageNotNAN, aa => if (aa > 9) ageSBl10 else "")
      }),
      Rule("userName", true, v => {
        if (v.matches("^[a-zA-Z0-9]+$")) "" else validUserName
      }),
      Rule("userName", true, v => {
        if (v.length > 2) "" else tooShortUserName
      }),
      Rule("passWord", true, p => {
        if (p.matches("^([a-z])*$"))
          ""
        else validPass
            }),
      Rule("passWord", true, p => {
        if (p.length > 2)
          ""
        else shortPass
      })
    )
    val params = Map("age" -> age, "userName" -> userName, "passWord" -> password)
    validateAll(params, rules)
  }
}
