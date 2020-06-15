package caching.redisCatsEffect

import cats.implicits._
import caching.model.Cat
import cats.effect.{ContextShift, Timer}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import scalacache._
import scalacache.redis.RedisCache
import _root_.redis.clients.jedis._
import scalacache.serialization.binary._
import scala.concurrent.duration._

/**
  * Created by Ilya Volynin on 14.06.2020 at 16:29.
  */
object CacheCEE1 extends App {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect

  implicit val mode: Mode[Task] = scalacache.CatsEffect.modes.async

  implicit val redisCache: Cache[Cat] = RedisCache("localhost", 6379)

  val c1 = Cat(1, "ilya1", "black")
  val c2 = Cat(2, "ilya2", "white")

  redisCache
    .put(c1.id)(c1, Some(1 second))
    .flatTap { _ =>
      Task.sleep(500 millis)
    }
    .flatMap(a => get(c1.id))
    .map {
      case Some(c) => println(s"cat $c found")
      case None    => println(s"no cats found ")
    }
    .flatTap { _ =>
      Task.sleep(600 millis)
    }
    .flatMap(a => get(c1.id))
    .map {
      case Some(c) => println(s"cat $c found")
      case None    => println(s"no cats found ")
    }
    .runSyncUnsafe()
}
