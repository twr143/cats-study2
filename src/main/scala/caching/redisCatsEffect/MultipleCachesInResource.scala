package caching.redisCatsEffect

import caching.model._
import cats.effect.{ContextShift, Timer}
import com.twitter.bijection.Bijection._
import com.twitter.bijection.GZippedBytes
import com.twitter.chill.KryoInjection
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scalacache._
import scalacache.redis.RedisCache
import scalacache.serialization.Codec

import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 14.06.2020 at 17:42.
  */
object MultipleCachesInResource extends App {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect

  implicit val mode: Mode[Task] = scalacache.CatsEffect.modes.async

  implicit val codec = new Codec[Animal] {
    def encode(value: Animal): Array[Byte] = (KryoInjection andThen bytes2GzippedBytes)(value).bytes
    def decode(bytes: Array[Byte]): Codec.DecodingResult[Animal] =
      Codec.tryDecode((GZippedBytes.apply _ andThen bytes2GzippedBytes.invert andThen KryoInjection.invert)(bytes).get.asInstanceOf[Animal])
  }

  implicit val redisCache: Cache[Animal] = RedisCache("localhost", 6379)

  (for {
    animals <- Task { (Cat(1, "ilyaBarsik", "black"), Dog(1, "ilyaSharick", "white")) }
    _ <- redisCache.put("$cat$" + animals._1.id)(animals._1, Some(1 second))
    _ <- redisCache.put("$dog$" + animals._2.id)(animals._2, Some(1 second))
    _ <- Task.sleep(500 millis)
    _ <- get("$cat$" + animals._1.id).map {
      case Some(c: Cat) => Task.now(println(s"cat $c found"))
      case None         => Task.now(println(s"no cats found "))
      case a            => Task.now(println(s"cat/dgo $a found"))
    }
    _ <- get("$dog$" + animals._2.id).map {
      case Some(d: Dog) => Task.now(println(s"dog $d found"))
      case None         => Task.now(println(s"no dogs found "))
      case a            => Task.now(println(s"cat/dgo $a found"))
    }
    _ <- Task.sleep(600 millis)
    _ <- get("$cat$" + animals._1.id)
      .map {
        case Some(c) => println(s"cat $c found")
        case None    => println(s"no cats found ")
      }

  } yield ()).runSyncUnsafe()
}
