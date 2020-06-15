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
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import _root_.redis.clients.jedis._

/**
  * Created by Ilya Volynin on 14.06.2020 at 19:45.
  */
object TwoRedisCaches extends App {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect

  implicit val mode: Mode[Task] = scalacache.CatsEffect.modes.async

  val catCache: Cache[Cat] = RedisCache(new JedisPool(new GenericObjectPoolConfig(), "localhost", 6379, Protocol.DEFAULT_TIMEOUT, null, 0))
  val dogCache: Cache[Dog] = RedisCache(new JedisPool(new GenericObjectPoolConfig(), "localhost", 6379, Protocol.DEFAULT_TIMEOUT, null, 1))

  val c1 = Cat(1, "ilya1Barsik", "black")
  val d1 = Dog(1, "ilya2Sharik", "white")

  (for {
    _ <- catCache.put(c1.id)(c1, Some(2 second))
    _ <- dogCache.put(d1.id)(d1, Some(1 second))
    _ <- Task.sleep(500 millis)
    _ <- catCache.get(c1.id).map {
      case Some(c) => println(s"cat $c found")
      case None    => println(s"no cats found ")
    }
    _ <- dogCache.get(d1.id).map {
      case Some(d) => println(s"dog $d found")
      case None    => println(s"no dogs found ")
    }
    _ <- Task.sleep(600 millis)
    _ <- catCache.get(c1.id).map {
      case Some(c) => println(s"cat $c found")
      case None    => println(s"no cats found ")
    }
    _ <- dogCache.get(d1.id).map {
      case Some(d) => println(s"dog $d found")
      case None    => println(s"no dogs found ")
    }
    _ <- catCache.close()
    _ <- dogCache.close()
  } yield ()).runSyncUnsafe()

}
