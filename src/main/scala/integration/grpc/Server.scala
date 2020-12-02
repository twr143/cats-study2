package integration.grpc

/**
  * Created by Ilya Volynin on 02.12.2020 at 9:12.
  */
import cats.effect.{ExitCode, IO, IOApp}
import fs2._
import io.grpc._
import io.grpc.protobuf.services.ProtoReflectionService
import grpc.model.hello._
import org.lyranthe.fs2_grpc.java_runtime.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends IOApp {
  class ExampleImplementation extends GreeterFs2Grpc[IO, Metadata] {
    override def sayHello(request: HelloRequest, clientHeaders: Metadata): IO[HelloResponse] = {
      IO(HelloResponse("Request name is: " + request.name))
    }

    override def sayHelloStream(request: Stream[IO, HelloRequest], clientHeaders: Metadata): Stream[IO, HelloResponse] = {
      request.evalMap(req => sayHello(req, clientHeaders))
    }
  }
  val helloService: ServerServiceDefinition =
    GreeterFs2Grpc.bindService(new ExampleImplementation)
  def run(args: scala.List[String]): cats.effect.IO[cats.effect.ExitCode] = {
    ServerBuilder
      .forPort(9999)
      .addService(helloService)
      //            .asInstanceOf[ServerBuilder[ServerServiceDefinition]]
      .addService(ProtoReflectionService.newInstance())
      .stream[IO]
      .evalMap(server => IO(server.start()))
      .evalMap(_ => IO.never)
      .compile
      .drain
      .map(_ => ExitCode.Success)
  }
}
