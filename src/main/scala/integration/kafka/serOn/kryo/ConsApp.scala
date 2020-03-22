package integration.kafka.serOn.kryo

import cats.effect.{ExitCode, IO, IOApp}
import com.twitter.bijection.Bijection._
import com.twitter.bijection.GZippedBytes
import com.twitter.chill.KryoInjection
import com.typesafe.scalalogging.StrictLogging
import fs2.kafka._
import integration.kafka.serOn.kryo.Model.Human

import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 21.03.2020 at 15:04.
  */
object ConsApp extends IOApp with StrictLogging {

  val valueS = Deserializer.instance[IO, Human] { (topic, headers, array) =>
    headers("kkk")
    IO.delay {
      println(s"topic = ${topic}, headers = $headers")
      (GZippedBytes.apply _ andThen bytes2GzippedBytes.invert andThen KryoInjection.invert)(array).get.asInstanceOf[Human]
    }
  }

  val consumerSettings =
    ConsumerSettings[IO, String, Human](keyDeserializer = Deserializer[IO, String], valueDeserializer = valueS)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9093")
      .withGroupId("group2")
  def processRecord(record: ConsumerRecord[String, Human]): IO[Unit] =
    IO.pure(logger.info(s"${record.key -> record.value}"))

  def run(args: List[String]): IO[ExitCode] = {
    val s =
      consumerStream[IO]
        .using(consumerSettings)
        .evalTap(_.subscribeTo("22marchPerson1"))
        .flatMap(_.stream)
        .mapAsync(25) { committable =>
          processRecord(committable.record).map(_ => committable.offset)
        }
        .through(commitBatchWithin(3, 1.seconds))
        .interruptAfter(2 seconds)
    s.compile.drain.map(_ => ExitCode.Success)

  }
}
