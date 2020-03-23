package integration.kafka.serOn.protobuf.app
import cats.effect.{ExitCode, IO, IOApp}
import com.google.protobuf.CodedInputStream
import com.typesafe.scalalogging.StrictLogging
import fs2.kafka._
import protomodels.person.Person
import protomodels.person2.Person2
import scala.concurrent.duration._
import com.google.protobuf.any.Any

/**
  * Created by Ilya Volynin on 22.03.2020 at 19:38.
  */
object ConsApp extends IOApp with StrictLogging {
  import protomodels.person.Person._
  import protomodels.person2.Person2._
  val valueS = Deserializer.instance[IO, scalapb.GeneratedMessage] { (topic, headers, array) =>
    IO.delay {
      val any = Any.parseFrom(array)
      if (any.is[Person]) any.unpack[Person]
      else if (any.is[Person2]) any.unpack[Person2]
      else Person("unpack failed", -1)
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
        .evalTap(_.subscribeTo("23marProto2"))
        .flatMap(_.stream)
        .mapAsync(25) { committable =>
          processRecord(committable.record).map(_ => committable.offset)
        }
        .through(commitBatchWithin(3, 1.seconds))
        .interruptAfter(2 seconds)
    s.compile.drain.map(_ => ExitCode.Success)

  }
}
