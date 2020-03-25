package integration.kafka.serOn.msgpack

import java.time.LocalDate

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import fs2.kafka._
import integration.kafka.serOn.msgpack.Model._
import upickle.default._

/**
  * Created by Ilya Volynin on 21.03.2020 at 13:31.
  */
object ProdApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

//    val tryDecode: scala.util.Try[Any] = KryoInjection.invert(bytes)
    val valueS = Serializer.instance[IO, HumanMsg] { (topic, headers, p) =>
      IO.pure {
//        println(s"раз дваgzipped array siz:${array.length}, ori size: ${ori.length}")
//        upickle.default.writeMsg[HumanMsg] _ andThen upack.write apply p
        Array()
      }
    }
    val producerSettings = ProducerSettings[IO, String, HumanMsg](keySerializer = Serializer[IO, String], valueSerializer = valueS)
      .withBootstrapServers("localhost:9093")
    val s = Stream(4, 5, 6)
      .map(i =>
        if (i % 2 == 0)
          PersonMsg("jdslkjhalkhjfalsdkjhflaskdhflskjh", AddressMsg(879123 * i, i.toString + " Lebedev St."))
        else
          PersonMsg5(
            "n:;dlskajfl;ksjfljh1827098 7oi;lkjs;fkas;/lfj27rlakf;ll21fafa08sd7f-a7sdf8asdof78fodpfoasdfp9839099a99087" + i,
            address = AddressMsg(
              i,
              "7 Lebedev St';lk'sl;kgdsf';gksogeojg';alsjgasgljasl;kas" +
                "f'sd's'asfa';dlfka;'slfka;slkf'asld;fa;'slkf'a;sldkfa;'slfk';sk"
            ),
            sex = true,
            color = ColorMsg(i % 3 + 1),
            if (i % 3 > 0) Some(MyLocalDateMsg(LocalDate.of(1982 + (i % 3), 3 + (i % 3), 25 + (i % 3)))) else None
          )
      )
      .evalMap(p =>
        IO(
          ProducerRecords.one(
            ProducerRecord("25MarMsg", p.address.no.toString, p)
              .withHeaders(Headers(Header("kkk", "vvv")))
          )
        )
      )
      .through(produce[IO, String, HumanMsg, Unit](producerSettings))

    s.compile.drain.map(_ => ExitCode.Success)

  }

}
