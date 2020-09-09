package distage
import distage.{Injector, ModuleDef, Roots}

import scala.io.StdIn

/**
  * Created by Ilya Volynin on 09.09.2020 at 20:22.
  */
object Basic {

  trait Greeter {
    def hello(name: String): Unit
  }

  final class PrintGreeter extends Greeter {
    override def hello(name: String) = println(s"Hello $name!")
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
    val HelloByeModule = new ModuleDef {
      make[Greeter].from[PrintGreeter]
      make[Byer].from[PrintByer]
      make[HelloByeApp] // `.from` is not required for concrete classes
    }
    val plan = Injector().plan(HelloByeModule, Roots.Everything)
    Injector().produce(plan).use { objects =>
      objects.get[HelloByeApp].run()
    }
  }
}
