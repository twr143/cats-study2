package integration.kafka.serOn.kryo

import java.time.LocalDate

import cats.effect.{ExitCode, IO, IOApp}
import com.twitter.bijection.Bijection._
import com.twitter.chill.KryoInjection
import fs2.Stream
import fs2.kafka._
import integration.kafka.serOn.kryo.Model.{Address, Color, MyLocalDate, Person5}

/**
  * Created by Ilya Volynin on 21.03.2020 at 13:31.
  */
object ProdApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

//    val tryDecode: scala.util.Try[Any] = KryoInjection.invert(bytes)
    val valueS = Serializer.instance[IO, Person5] { (topic, headers, p) =>
      IO.pure {
        val ori = KryoInjection(p)
        val array = (KryoInjection andThen bytes2GzippedBytes)(p).bytes
        println(s"раз дваgzipped array siz:${array.length}, ori size: ${ori.length}")
        array
      }
    }
    val producerSettings = ProducerSettings[IO, String, Person5](keySerializer = Serializer[IO, String], valueSerializer = valueS)
      .withBootstrapServers("localhost:9093")
    val s = Stream(4, 5, 6)
      .map(i =>
        Person5(
          "n:;dlskajfl;ksjfljh1827098 7oi;lkjs;fkas;/lfj27rlakf;ll21fafa08sd7f-a7sdf8asdof78fodpfoasdfp9839099a99087" + i,
          address = Address(
            i,
            "7 Lebedev St';lk'sl;kgdsf';gksogeojg';alsjgasgljasl;kas" +
              "f'sd's'asfa';dlfka;'slfka;slkf'asld;fa;'slkf'a;sldkfa;'slfk';sk"
          ),
          sex = true,
          color = Color(i % 3 + 1),
          if (i % 3 > 0) Some(MyLocalDate(LocalDate.of(1982 + (i % 3), 3 + (i % 3), 25 + (i % 3)))) else None
        )
      )
      .evalMap(p =>
        IO(
          ProducerRecords.one(
            ProducerRecord("22marchPerson1", p.address.no.toString, p)
              .withHeaders(Headers(Header("kkk", "vvv")))
          )
        )
      )
      .through(produce[IO, String, Person5, Unit](producerSettings))

    s.compile.drain.map(_ => ExitCode.Success)

  }

}
