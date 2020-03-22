package integration.kafka.serOn.protobuf.app
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging
import fs2.kafka._
import protomodels.person.Person
import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 22.03.2020 at 19:38.
  */
object ConsApp extends IOApp with StrictLogging {
  val valueS = Deserializer.instance[IO, scalapb.GeneratedMessage] { (topic, headers, array) =>
    headers("kkk")
    IO.delay {
      println(s"topic = ${topic}, headers = $headers")
      Person.parseFrom(array)
    }
  }

  val consumerSettings =
    ConsumerSettings[IO, String, scalapb.GeneratedMessage](keyDeserializer = Deserializer[IO, String], valueDeserializer = valueS)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9093")
      .withGroupId("group2")
  def processRecord(record: ConsumerRecord[String, scalapb.GeneratedMessage]): IO[Unit] =
    IO.pure(logger.info(s"${record.key -> record.value}"))

  def run(args: List[String]): IO[ExitCode] = {
    val s =
      consumerStream[IO]
        .using(consumerSettings)
        .evalTap(_.subscribeTo("22marProtoPerson1"))
        .flatMap(_.stream)
        .mapAsync(25) { committable =>
          processRecord(committable.record).map(_ => committable.offset)
        }
        .through(commitBatchWithin(3, 1.seconds))
        .interruptAfter(2 seconds)
    s.compile.drain.map(_ => ExitCode.Success)

  }
}
