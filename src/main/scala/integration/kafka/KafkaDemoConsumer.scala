package integration.kafka
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging
import fs2._
import cats.syntax.functor._
import fs2.kafka._
import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 20.03.2020 at 21:43.
  */
object KafkaDemoConsumer extends IOApp with StrictLogging {
  val consumerSettings =
    ConsumerSettings[IO, String, String]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9093")
      .withGroupId("group")
  def processRecord(record: ConsumerRecord[String, String]): IO[Unit] =
    IO.pure(logger.info(s"${record.key -> record.value}"))

  def run(args: List[String]): IO[ExitCode] = {
    val s =
      consumerStream[IO]
        .using(consumerSettings)
        .evalTap(_.subscribeTo("demoFs2Kafka"))
        .flatMap(_.stream)
        .mapAsync(25) { committable =>
          processRecord(committable.record).map(_ => committable.offset)
        }
        .through(commitBatchWithin(3, 1.seconds))
        .interruptAfter(2 seconds)
    s.compile.drain.map(_ => ExitCode.Success)

  }
}
