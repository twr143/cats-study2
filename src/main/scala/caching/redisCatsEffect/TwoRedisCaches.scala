package caching.redisCatsEffect
import caching.model._
import caching.redisCatsEffect.CacheCEE1.redisCache
import cats.effect.{ContextShift, Timer}
import monix.eval.Task
import scalacache.{Cache, Flags, Mode, get}
import monix.execution.Scheduler.Implicits.global
import redis.clients.jedis.JedisPool
import scalacache.redis.RedisCache
import scalacache.serialization.binary._

import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 14.06.2020 at 19:45.
  */
object TwoRedisCaches extends App {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect

  val catMode: Mode[Task] = scalacache.CatsEffect.modes.async
  val dogMode: Mode[Task] = scalacache.CatsEffect.modes.async

  val catCache: Cache[Cat] = new RedisCache(new JedisPool("localhost", 6379))
  val dogCache: Cache[Dog] = new RedisCache(new JedisPool("localhost", 6379))

  val c1 = Cat(1, "ilya1Barsik", "black")
  val d1 = Dog(1, "ilya2Sharik", "white")

  (for {
    _ <- catCache.put[Task](c1.id)(c1, Some(1 second))(catMode, Flags.defaultFlags)
    _ <- dogCache.put[Task](d1.id)(d1, Some(1 second))(dogMode, Flags.defaultFlags)
    _ <- Task.sleep(500 millis)
    _ <- get(c1.id)(catCache, catMode, Flags.defaultFlags).map {
      case Some(c) => println(s"cat $c found")
      case None    => println(s"no cats found ")
    }
    _ <- Task.sleep(600 millis)
    _ <- get(c1.id)(catCache, catMode, Flags.defaultFlags).map {
      case Some(c) => println(s"cat $c found")
      case None    => println(s"no cats found ")
    }

  } yield ()).runSyncUnsafe()

}
