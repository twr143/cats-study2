package userReg

/**
  * Created by Ilya Volynin on 13.12.2019 at 13:29.
  */
object UserValidationE1 {

  def main(args: Array[String]): Unit = {
    println(FormValidatorNec.validateForm(
      username = "Joe",
      password = "Passw0r$1234",
      firstName = "John",
      lastName = "Doe",
      age = 21
    ))

    println(FormValidatorNec.validateForm(
      username = "Joe%%%",
      password = "password",
      firstName = "John",
      lastName = "Doe",
      age = 21
    ))
  }
}
