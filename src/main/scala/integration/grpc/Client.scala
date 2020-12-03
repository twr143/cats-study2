package integration.grpc
import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import grpc.model.hello.{GreeterFs2Grpc, HelloRequest}
import io.grpc._
import fs2._
import org.lyranthe.fs2_grpc.java_runtime.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Ilya Volynin on 02.12.2020 at 12:15.
  */
object Client extends IOApp {
  val address = "127.0.0.1:8081"
  val managedChannelStream: Stream[IO, ManagedChannel] =
    ManagedChannelBuilder
      .forTarget(address)
      .defaultLoadBalancingPolicy("round_robin")
      .usePlaintext()
      .stream[IO]

  def runProgram(helloStub: GreeterFs2Grpc[IO, Metadata]): IO[Unit] = {
    for {
      response <- helloStub.sayHello(HelloRequest("Ilyusha V"), new Metadata())
      _ <- IO(println(response.greeting))
      _ <- helloStub
        .sayHelloStream(
          Stream
            .iterate(0)(_ + 1)
            .map(a => HelloRequest("blah", a))
            .take(10),
          new Metadata()
        )
        .map(r => println(r.greeting))
        .compile
        .drain

    } yield ()
  }

  def run(args: scala.List[String]): cats.effect.IO[cats.effect.ExitCode] = {
    (for {
      managedChannel <- managedChannelStream
      helloStub = GreeterFs2Grpc.stub[IO](managedChannel)
      helloStu2 = GreeterFs2Grpc.stub[IO](managedChannel)
      _ <- Stream.eval(runProgram(helloStub))
      _ <- Stream.eval(runProgram(helloStu2))
    } yield ExitCode.Success).compile.drain
      .map(_ => ExitCode.Success)

  }
}
