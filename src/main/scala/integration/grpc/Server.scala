package integration.grpc

/**
  * Created by Ilya Volynin on 02.12.2020 at 9:12.
  */
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging
import fs2._
import io.grpc._
import io.grpc.protobuf.services.ProtoReflectionService
import grpc.model.hello._
import org.lyranthe.fs2_grpc.java_runtime.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends IOApp with StrictLogging {
  class ExampleImplementation(port: Int) extends GreeterFs2Grpc[IO, Metadata] {
    override def sayHello(request: HelloRequest, clientHeaders: Metadata): IO[HelloResponse] = {
      IO(HelloResponse("Serial is: " + request.serial + " " + port))
    }

    override def sayHelloStream(request: Stream[IO, HelloRequest], clientHeaders: Metadata): Stream[IO, HelloResponse] = {
      request.evalMap(req => sayHello(req, clientHeaders))
    }
  }
  def helloService(port: Int): ServerServiceDefinition =
    GreeterFs2Grpc.bindService(new ExampleImplementation(port))
  def run(args: scala.List[String]): cats.effect.IO[cats.effect.ExitCode] = {
    args.size match {
      case 1 =>
        ServerBuilder
          .forPort(args(0).toInt)
          .addService(helloService(args(0).toInt))
          .addService(ProtoReflectionService.newInstance())
          .stream[IO]
          .evalMap(server => IO(server.start()))
          .evalMap(_ =>
            IO {
              logger.warn(s"${System.getProperty("os.name")} Press Ctrl+Z to exit...")
              while (System.in.read() != -1) {}
              logger.warn("Received end-of-file on stdin. Exiting")
              // optional shutdown code here
            }
          )
          .compile
          .drain
          .map(_ => ExitCode.Success)
      case _ =>
        IO {
          logger.warn("Please provide a port as an argument. Exiting")
          ExitCode.Success
        }
    }
  }
}
