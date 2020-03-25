package integration.kafka.serOn.msgpack

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging
import fs2.kafka._
import integration.kafka.serOn.msgpack.Model._

import scala.concurrent.duration._
import upack.Readable

/**
  * Created by Ilya Volynin on 21.03.2020 at 15:04.
  */
object ConsApp extends IOApp with StrictLogging {

  val valueS = Deserializer.instance[IO, HumanMsg] { (topic, headers, array) =>
    IO.delay {
      println(s"topic = ${topic}, headers = $headers")
//      Readable.fromByteArray _ andThen upickle.default.readBinary apply array
      PersonMsg("", AddressMsg(1, ""))
    }
  }

  val consumerSettings =
    ConsumerSettings[IO, String, HumanMsg](keyDeserializer = Deserializer[IO, String], valueDeserializer = valueS)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9093")
      .withGroupId("group2")
  def processRecord(record: ConsumerRecord[String, HumanMsg]): IO[Unit] =
    IO.pure(logger.info(s"${record.key -> record.value}"))

  def run(args: List[String]): IO[ExitCode] = {
    val s =
      consumerStream[IO]
        .using(consumerSettings)
        .evalTap(_.subscribeTo("25MarMsg"))
        .flatMap(_.stream)
        .mapAsync(25) { committable =>
          processRecord(committable.record).map(_ => committable.offset)
        }
        .through(commitBatchWithin(3, 1.seconds))
        .interruptAfter(2 seconds)
    s.compile.drain.map(_ => ExitCode.Success)

  }
}
