package integration.kafka.serOn
import java.util.Properties

import cats.effect.{ExitCode, IO, IOApp}
import com.twitter.bijection.GZippedBytes
import fs2.Stream
import fs2.kafka._
import integration.kafka.serOn.Model.{Address, Person}
import com.twitter.chill.KryoInjection
import com.twitter.bijection.Bijection._

/**
  * Created by Ilya Volynin on 21.03.2020 at 13:31.
  */
object ProdApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

//    val tryDecode: scala.util.Try[Any] = KryoInjection.invert(bytes)
    val valueS = Serializer.instance[IO, Person] { (topic, headers, p) =>
      IO.pure {
        val ori = KryoInjection(p)
        val array = (KryoInjection andThen bytes2GzippedBytes)(p).bytes
        println(s"gzipped array siz:${array.length}, ori size: ${ori.length}")
        array
      }
    }
    val producerSettings = ProducerSettings[IO, String, Person](keySerializer = Serializer[IO, String], valueSerializer = valueS)
      .withBootstrapServers("localhost:9093")
    val s = Stream(20, 21, 22)
      .map(i =>
        Person(
          "n:;dlskajfl;ksjfljh1827098 7oi;lkjs;fkas;/lfj27rlakf;ll21fafa08sd7f-a7sdf8asdof78fodpfoasdfp9839099a99087" + i,
          address = Address(
            i,
            "7 Lebedev St';lk'sl;kgdsf';gksogeojg';alsjgasgljasl;kas" +
              "f'sd'аэждлфваэыждалыаыждыфджалэыфжвдлаэфыджвлаыва" +
              "ыфважыдважыдлаэжывлдаф" +
              "ыжвалдфыэвдаэыжвдлафжэыдвлаэфжывлдаэжфыдвлаэфджыаэжфдылважэдфлывжаэдфывжадлфыэджалэфыжвдалфэыжвдаэжыфдвлаэжфдыл" +
              "лыфджаолфывжаэд" +
              "ыфджвлаождылаождывлаождывлоаэ" +
              "ыфвлаыжвлаофджывлаожфдывлаофджывлао" +
              "лдорывфадлорывдалоыврдал" +
              "фывлдаывдфаодывжлаоы" +
              "жывлоаждылаожфдывлао" +
              ";fksas'asfa'slfka's;dlfka;'slfka;slkf'asld;fa;'slkf'a;sldkfa;'slfk';sk"
          )
        )
      )
      .evalMap(p =>
        IO(
          ProducerRecords.one(
            ProducerRecord("21marchPerson5", p.address.no.toString, p)
              .withHeaders(Headers(Header("kkk", "vvv")))
          )
        )
      )
      .through(produce[IO, String, Person, Unit](producerSettings))

    s.compile.drain.map(_ => ExitCode.Success)

  }

}
