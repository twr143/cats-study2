package shift

/**
  * Created by twr143 on 27.05.2021 at 10:05.
  */
import java.util.concurrent.{Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicInteger

/**
  * Thread factory that creates threads that are named.  Threads will be named with the format:
  *
  * {name}-{threadNo}
  *
  * where threadNo is an integer starting from one.
  */
class NamedThreadFactory(name: String, daemon: Boolean) extends ThreadFactory {

  private val parentGroup = Option(System.getSecurityManager).fold(Thread.currentThread().getThreadGroup)(_.getThreadGroup)

  private val threadGroup = new ThreadGroup(parentGroup, name)
  private val threadCount = new AtomicInteger(1)
  private val threadHash = Integer.toUnsignedString(this.hashCode())

  override def newThread(r: Runnable): Thread = {
    val newThreadNumber = threadCount.getAndIncrement()

    val thread = new Thread(threadGroup, r)
    thread.setName(s"$name-$newThreadNumber-$threadHash")
    thread.setDaemon(daemon)

    thread
  }

}
