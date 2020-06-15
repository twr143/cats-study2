package caching.guava
import java.util.concurrent.TimeUnit
import caching.model._

import scalacache._
import cats.effect.{ContextShift, Timer}
import com.typesafe.scalalogging.StrictLogging
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scalacache.serialization.binary._

import scala.concurrent.duration._
import scalacache.guava._
import com.google.common.cache.CacheBuilder

/**
  * Created by Ilya Volynin on 15.06.2020 at 9:26.
  */
object GuavaCEE1 extends App with StrictLogging {
  implicit val cs: ContextShift[Task] = Task.contextShift(global)

  implicit val timer: Timer[Task] = Task.timer(global)
  implicit val effect = Task.catsEffect

  implicit val mode: Mode[Task] = scalacache.CatsEffect.modes.async

  val c1 = Cat(1, "ilya1Barsik", "black")
  val d1 = Dog(1, "ilya2Sharik", "white")

  (for {
    cacheCat <- Task.delay(CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[Cat]]).map(GuavaCache(_))
    cacheDog <- Task.delay(CacheBuilder.newBuilder().maximumSize(10000L).expireAfterWrite(1, TimeUnit.SECONDS).build[String, Entry[Dog]]).map(GuavaCache(_))
    _ <- cacheCat.put(c1.id)(c1, Some(2 second)) //(catMode, Flags.defaultFlags)
    _ <- cacheDog.put(d1.id)(d1, Some(1 second)) //(dogMode, Flags.defaultFlags)
    _ <- Task.sleep(500 millis)
    _ <- get(c1.id)(cacheCat, mode, Flags.defaultFlags).map {
      case Some(c) => logger.info(s"cat $c found")
      case None    => logger.info(s"no cats found ")
    }
    _ <- get(d1.id)(cacheDog, mode, Flags.defaultFlags).map {
      case Some(d) => logger.info(s"dog $d found")
      case None    => logger.info(s"no dogs found ")
    }
    _ <- Task.sleep(600 millis)
    _ <- get(c1.id)(cacheCat, mode, Flags.defaultFlags).map {
      case Some(c) => logger.info(s"cat $c found")
      case None    => logger.info(s"no cats found ")
    }
    _ <- get(d1.id)(cacheDog, mode, Flags.defaultFlags).map {
      case Some(d) => logger.info(s"dog $d found")
      case None    => logger.info(s"no dogs found ")
    }

  } yield ()).runSyncUnsafe()

}
