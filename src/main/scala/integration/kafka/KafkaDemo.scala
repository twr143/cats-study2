package integration.kafka

import cats.implicits._
import fs2._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.kafka._
import scala.concurrent.duration._

object KafkaDemo extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    def processRecord(record: ConsumerRecord[String, String]): IO[(String, String)] =
      IO.pure(record.key -> record.value)

    val consumerSettings =
      ConsumerSettings[IO, String, String]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers("localhost:9093")
        .withGroupId("group")

    val producerSettings =
      ProducerSettings[IO, String, String]
        .withBootstrapServers("localhost:9093")

//    val stream =
//      consumerStream[IO]
//        .using(consumerSettings)
//        .evalTap(_.subscribeTo("demoFs2Kafka"))
//        .flatMap(_.stream)
//        .mapAsync(25) { committable =>
//          processRecord(committable.record)
//            .map {
//              case (key, value) =>
//                val record = ProducerRecord("demoFs2Kafka", key, value)
//                ProducerRecords.one(record, committable.offset)
//            }
//        }
    val s = Stream(1, 2, 3)
      .evalMap(i => IO(ProducerRecords.one(ProducerRecord("demoFs2Kafka", i + "", i * 10 + ""))))
      .through(produce[IO, String, String, Unit](producerSettings))
//      .through(commitBatchWithin(500, 2.seconds))
//
    s.compile.drain.map(_ => ExitCode.Success)

  }
}
