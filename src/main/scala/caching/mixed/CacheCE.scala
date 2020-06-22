package caching.mixed
import cats.implicits._
import java.util.concurrent.TimeUnit

import caching.model._
import scalacache._
import cats.effect.{ContextShift, Timer}
import com.typesafe.scalalogging.StrictLogging
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._
import scalacache.caffeine._
import com.github.benmanes.caffeine.cache.Caffeine
import _root_.redis.clients.jedis._
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import scalacache.redis.RedisCache
import scalacache.serialization.Codec
import scalacache.serialization.binary._

/**
  * Created by Ilya Volynin on 15.06.2020 at 13:48.
  */
object CacheCE {

  implicit val cs: ContextShift[Task] = Task.contextShift(global)
  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect
  implicit val mode: Mode[Task] = scalacache.CatsEffect.modes.async
  val c1 = Cat(1, "ilya1Barsik", "black")

  def createCache[T](isInMemory: Boolean, redisDBIndex: Int = 0)(implicit codec: Codec[T]): Cache[T] =
    if (isInMemory)
      CaffeineCache(Caffeine.newBuilder().maximumSize(10000L).build[String, Entry[T]])
    else
      RedisCache[T](new JedisPool(new GenericObjectPoolConfig(), "localhost", 6379, Protocol.DEFAULT_TIMEOUT, null, redisDBIndex))

}
