package integration.kafka.serOn.protobuf.app
import java.time.LocalDate

import cats.effect.{ExitCode, IO, IOApp}
import com.google.protobuf.any.Any
import scalapb.GeneratedMessage
import fs2.Stream
import fs2.kafka._
import protomodels.person.Person
import protomodels.person.Person.PhoneNumber
import protomodels.person2.Person2.PhoneNumber2
import protomodels.person2.Person2

/**
  * Created by Ilya Volynin on 22.03.2020 at 19:10.
  */
object ProdApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val p = Person("Ilya", 123, None, List(PhoneNumber("7900000001"), PhoneNumber("7900000002")))
    //    val tryDecode: scala.util.Try[Any] = KryoInjection.invert(bytes)
    val valueS = Serializer.instance[IO, GeneratedMessage] { (topic, headers, p) =>
      { IO.pure(Any.pack(p).toByteArray) }
    }
    val producerSettings = ProducerSettings[IO, String, GeneratedMessage](keySerializer = Serializer[IO, String], valueSerializer = valueS)
      .withBootstrapServers("localhost:9093")
    val s = Stream(4, 5, 6, 7)
      .map(i =>
        if (i % 2 == 0)
          Person(s"Ilya$i", 123 * i, None, List(PhoneNumber("7900000001"), PhoneNumber("7900000002")))
        else
          Person2(s"2Ilya$i", 123 * i, Some(s"$i@mail.com"), List(PhoneNumber2("7900000001"), PhoneNumber2("7900000002")))
      )
      .evalMap {
        case p: Person =>
          IO(ProducerRecords.one(ProducerRecord("23marProto2", p.name, p).withHeaders(Headers(Header("p", "vvv")))))
        case p: Person2 =>
          IO(ProducerRecords.one(ProducerRecord("23marProto2", p.name, p).withHeaders(Headers(Header("p2", "vvv")))))
      }
      .through(produce[IO, String, GeneratedMessage, Unit](producerSettings))
    s.compile.drain.map(_ => ExitCode.Success)
  }
}
