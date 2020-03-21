package integration.kafka

import cats.implicits._
import fs2._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.kafka._
import scala.concurrent.duration._

object KafkaDemo extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val producerSettings =
      ProducerSettings[IO, String, String]
        .withBootstrapServers("localhost:9093")

    val s = Stream(1, 2, 3)
      .evalMap(i => IO(ProducerRecords.one(ProducerRecord("demoFs2Kafka", i + "", i * 10 + ""))))
      .through(produce[IO, String, String, Unit](producerSettings))
//      .through(commitBatchWithin(500, 2.seconds))
//
    s.compile.drain.map(_ => ExitCode.Success)

  }
}
