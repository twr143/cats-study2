package env

/**
  * Created by Ilya Volynin on 22.12.2020 at 11:05.
  */
object Variables {
  def main(args: Array[String]): Unit = {
    println(sys.env.getOrElse("test_var", "not found"))
  }
}
