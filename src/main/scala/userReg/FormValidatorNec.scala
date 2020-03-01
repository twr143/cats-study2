package userReg
import cats.data._
import cats.data.Validated._
import cats.implicits._
/**
  * Created by Ilya Volynin on 13.12.2019 at 13:23.
  */
sealed trait FormValidatorNec {

  type ValidationResult[A] = ValidatedNec[DomainValidation, A]

  private def validateUserName(userName: String): ValidationResult[String] =
    if (userName.matches("^[a-zA-Z0-9]+$")) userName.validNec else UsernameHasSpecialCharacters.invalidNec

  private def validatePassword(password: String): ValidationResult[String] =
    if (password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) password.validNec
    else PasswordDoesNotMeetCriteria.invalidNec

  private def validateFirstName(firstName: String): ValidationResult[String] =
    if (firstName.matches("^[a-zA-Z]+$")) firstName.validNec else FirstNameHasSpecialCharacters.invalidNec

  private def validateLastName(lastName: String): ValidationResult[String] =
    if (lastName.matches("^[a-zA-Z]+$")) lastName.validNec else LastNameHasSpecialCharacters.invalidNec

  private def validateAge(age: Int): ValidationResult[Int] =
    if (age >= 18 && age <= 75) age.validNec else AgeIsInvalid.invalidNec

  def validateForm(username: String, password: String, firstName: String, lastName: String, age: Int): ValidationResult[RegistrationData] = {
    (validateUserName(username),
    validatePassword(password),
    validateFirstName(firstName),
    validateLastName(lastName),
    validateAge(age)).mapN(RegistrationData)
  }

}

object FormValidatorNec extends FormValidatorNec