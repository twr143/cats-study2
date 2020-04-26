package userRegDyn

/**
  * Created by Ilya Volynin on 26.04.2020 at 13:02.
  */
case class Rule(fieldName: String, mandatory: Boolean, validFunc: String => String)
