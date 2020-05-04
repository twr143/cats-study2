package userRegDyn
import cats.implicits._
import cats.data._

/**
  * Created by Ilya Volynin on 26.04.2020 at 13:11.
  */
object FieldValidatorNec {

  def validateField(fieldsFieldValue: Map[String, String], rule: Rule): ValidatedNec[String, String] = {
    val check = rule.validFunc(fieldsFieldValue(rule.fieldName))
    if (rule.mandatory)
      if (check.isEmpty)
        "".validNec
      else check.invalidNec
    else "".validNec
  }
  def validateAll(fieldsFieldValue: Map[String, String], rules: List[Rule]): ValidatedNec[String, String] = {
    rules.foldLeft("".validNec[String])((chain, rule) => chain.combine(validateField(fieldsFieldValue, rule)))
  }

}
