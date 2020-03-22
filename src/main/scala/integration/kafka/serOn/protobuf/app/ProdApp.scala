package integration.kafka.serOn.protobuf.app
import java.time.LocalDate

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import fs2.kafka._
import protomodels.person.Person
import protomodels.person.Person.PhoneNumber

/**
  * Created by Ilya Volynin on 22.03.2020 at 19:10.
  */
object ProdApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val p = Person("Ilya", 123, None, List(PhoneNumber("7900000001"), PhoneNumber("7900000002")))
    //    val tryDecode: scala.util.Try[Any] = KryoInjection.invert(bytes)
    val valueS = Serializer.instance[IO, Person] { (topic, headers, p) =>
      IO.pure(p.toByteArray)
    }
    val producerSettings = ProducerSettings[IO, String, Person](keySerializer = Serializer[IO, String], valueSerializer = valueS)
      .withBootstrapServers("localhost:9093")
    val s = Stream(4, 5, 6)
      .map(i => Person(s"Ilya$i", 123 * i, None, List(PhoneNumber("7900000001"), PhoneNumber("7900000002"))))
      .evalMap(p =>
        IO(
          ProducerRecords.one(
            ProducerRecord("22marProtoPerson1", p.name, p)
              .withHeaders(Headers(Header("kkk", "vvv")))
          )
        )
      )
      .through(produce[IO, String, Person, Unit](producerSettings))
    s.compile.drain.map(_ => ExitCode.Success)
  }
}
