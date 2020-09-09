package distage
import scala.io.StdIn

/**
  * Created by Ilya Volynin on 09.09.2020 at 20:39.
  */
object DAxis {
  object Style extends Axis {
    case object AllCaps extends AxisValueDef
    case object Normal extends AxisValueDef
  }

  trait Greeter {
    def hello(name: String): Unit
  }

  final class PrintGreeter extends Greeter {
    override def hello(name: String) = println(s"Hello $name!")
  }
  class AllCapsGreeter extends Greeter {
    def hello(name: String) = println(s"HELLO ${name.toUpperCase}")
  }

  trait Byer {
    def bye(name: String): Unit
  }

  final class PrintByer extends Byer {
    override def bye(name: String) = println(s"Bye $name!")
  }

  final class HelloByeApp(greeter: Greeter, byer: Byer) {
    def run(): Unit = {
      println("What's your name?")
      val name = StdIn.readLine()

      greeter.hello(name)
      byer.bye(name)
    }
  }

  def main(args: Array[String]): Unit = {
    val TwoImplsModule = new ModuleDef {
      make[Greeter]
        .tagged(Style.Normal)
        .from[PrintGreeter]

      make[Greeter]
        .tagged(Style.AllCaps)
        .from[AllCapsGreeter]
    }
    val HelloByeModule = new ModuleDef {
      make[Greeter].from[PrintGreeter]
      make[Byer].from[PrintByer]
      make[HelloByeApp] // `.from` is not required for concrete classes
    }
    val CombinedModule = HelloByeModule overridenBy TwoImplsModule

    Injector(Activation(Style -> Style.Normal))
      .produceGet[HelloByeApp](CombinedModule)
      .use(_.run())
  }
}
