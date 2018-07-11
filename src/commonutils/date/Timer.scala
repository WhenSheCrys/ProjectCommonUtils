package commonutils.date

import java.text.SimpleDateFormat
import java.util.concurrent.{Executors, ConcurrentHashMap => Map, TimeUnit => JavaTimeUnit}
import java.util.{Date, TimerTask, Timer => JavaTimer}

import commonutils.date.Timer.TimerType.TimerType
import org.apache.log4j.Logger

object Timer {

  private final val logger = Logger.getLogger(this.getClass)

  private final val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  /* 线程安全的Map，用来存放定时器 */
  private final val taskMap: Map[String, CustomTimer] = new Map[String, CustomTimer]()

  /**
    * 创建新的定时器实例
    *
    * @param timerType 定时器类型，简单定时器和线程池定时器
    * @param name
    * @return
    */
  def getInstance(timerType: TimerType.Value, name: String): CustomTimer = {
    synchronized {
      if (taskMap.contains(name)) {
        logger.warn(s"timer:$name already exists!")
        taskMap.get(name)
        //throw new RuntimeException(s"timer:$name already exists!")
      } else {
        val res = timerType match {
          case TimerType.SimpleTimer => new SimpleTimer(name)
          case _ => new ThreadTimer(timerType, name)
        }
        taskMap.put(name, res)
        logger.info(s"created timer:$name....")
        res
      }
    }
  }

  /**
    * 取消定时器
    *
    * @param name
    * @return
    */
  def cancle(name: String): Boolean = {
    if (taskMap.contains(name)) {
      logger.warn(s"timer:$name will be cancled...")
      try {
        val timer = taskMap.get(name)
        timer.shutdown()
        taskMap.remove(name)
        true
      } catch {
        case e: Exception =>
          logger.error(s"error occured when cancle timer:$name, reason:${e.getMessage}")
          throw e
          false
      }
    } else {
      logger.warn(s"timer:$name may be cancled or failed, or even no created, nothing to cancle!")
      true
    }
  }

  /**
    * 判断定时器是否正在运行
    *
    * @param name
    * @return
    */
  def isRuuning(name: String): Boolean = {
    taskMap.contains(name)
  }

  /**
    * 定时器接口
    */
  protected[this] trait CustomTimer {

    /**
      * 立即执行
      *
      * @param task
      */
    def scheduleImmediately(task: Runnable): Unit

    /**
      * 延迟执行
      *
      * @param task
      * @param fixedDelay
      */
    def scheduleWithDelay(task: Runnable, fixedDelay: Long): Unit

    /**
      * 延迟执行
      *
      * @param task
      * @param firstTime
      */
    def scheduleWithDelay(task: Runnable, firstTime: Date): Unit

    /**
      * 固定延迟之后周期性执行
      *
      * @param task   要调度的任务
      * @param delay  延迟，毫秒
      * @param period 周期，毫秒
      */
    def scheduleAtFixedRate(task: Runnable, delay: Long, period: Long): Unit

    /**
      * 固定延迟之后周期性执行
      *
      * @param task      要调度的任务
      * @param firstTime 第一次执行的时间
      * @param period    执行周期
      */
    def scheduleAtFixedRate(task: Runnable, firstTime: Date, period: Long): Unit

    /**
      * 停止执行
      */
    def shutdown(): Unit

    protected def getTimerTask(task: Runnable): TimerTask = {
      new TimerTask {
        override def run(): Unit = {
          task.run()
        }
      }
    }

  }

  private[this] class SimpleTimer(name: String) extends CustomTimer {

    private lazy val timer = new JavaTimer(name, false)

    override def scheduleImmediately(task: Runnable): Unit = {
      try {
        timer.schedule(getTimerTask(task), 0L)
        logger.info(s"timer:$name scheduled successfully!")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleWithDelay(task: Runnable, fixedDelay: Long): Unit = {
      try {
        val taskRunTime = sdf.format(new Date(System.currentTimeMillis() + fixedDelay))
        timer.schedule(getTimerTask(task), fixedDelay)
        logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleAtFixedRate(task: Runnable, delay: Long, period: Long): Unit = {
      try {
        val taskRunTime = sdf.format(new Date(System.currentTimeMillis() + delay))
        timer.schedule(getTimerTask(task), delay, period)
        logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleAtFixedRate(task: Runnable, firstTime: Date, period: Long): Unit = {
      try {
        val taskRunTime = sdf.format(firstTime)
        timer.schedule(getTimerTask(task), firstTime, period)
        logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleWithDelay(task: Runnable, firstTime: Date): Unit = {
      try {
        val taskRunTime = sdf.format(firstTime)
        timer.schedule(getTimerTask(task), firstTime)
        logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def shutdown(): Unit = {
      timer.cancel()
    }

    override def toString: String = {
      s"timer:$name, type:${TimerType.SimpleTimer}"
    }

  }

  private[this] class ThreadTimer(timerType: TimerType, name: String) extends CustomTimer {

    private lazy val prosessers = Runtime.getRuntime.availableProcessors()

    private lazy val timer = timerType match {
      case TimerType.SingleThreadTimer => Executors.newSingleThreadScheduledExecutor()
      case TimerType.MultiThreadTimer => Executors.newScheduledThreadPool(prosessers * 2)
      case _ => throw new IllegalArgumentException(s"argument timerType must be ${TimerType.SingleThreadTimer}")
    }

    override def scheduleImmediately(task: Runnable): Unit = {
      try {
        timer.schedule(task, 0L, JavaTimeUnit.MILLISECONDS)
        logger.info(s"timer:$name scheduled successfully!")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleWithDelay(task: Runnable, firstTime: Date): Unit = {
      try {
        val nowTime = new Date()
        if (firstTime.before(nowTime)) {
          val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          val f = sdf.format(firstTime)
          val n = sdf.format(nowTime)
          logger.error(s"argument firstTime:$f is earlier than now time:$n")
          throw new IllegalArgumentException(s"argument firstTime:$f is earlier than now time:$n")
        } else {
          val taskRunTime = sdf.format(firstTime)
          val delay = firstTime.getTime - nowTime.getTime
          logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
          scheduleWithDelay(task, delay)
        }
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleWithDelay(task: Runnable, fixedDelay: Long): Unit = {
      try {
        val taskRunTime = sdf.format(new Date(System.currentTimeMillis() + fixedDelay))
        timer.schedule(task, fixedDelay, JavaTimeUnit.MILLISECONDS)
        logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleAtFixedRate(task: Runnable, firstTime: Date, period: Long): Unit = {
      try {
        val nowTime = new Date()
        if (firstTime.before(nowTime)) {
          val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          val f = sdf.format(firstTime)
          val n = sdf.format(nowTime)
          throw new IllegalArgumentException(s"argument firstTime:$f is earlier than now time:$n")
        } else {
          val taskRunTime = sdf.format(firstTime)
          val delay = firstTime.getTime - nowTime.getTime
          scheduleAtFixedRate(task, delay, period)
          logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
        }
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def scheduleAtFixedRate(task: Runnable, delay: Long, period: Long): Unit = {
      try {
        val taskRunTime = sdf.format(new Date(System.currentTimeMillis() + delay))
        timer.scheduleAtFixedRate(task, delay, period, JavaTimeUnit.MICROSECONDS)
        logger.info(s"timer:$name scheduled successfully! task will run at $taskRunTime")
      } catch {
        case e: Exception =>
          logger.error(s"Exception when schedule timer, reason:${e.getMessage}")
          taskMap.remove(name)
      }
    }

    override def shutdown(): Unit = {
      timer.shutdown()
    }

    override def toString: String = {
      s"timer:$name, type:$timerType"
    }

  }

  object TimerType extends Enumeration {
    type TimerType = TimerType.Value

    val SimpleTimer = Value(0, "SIMPLETIMER")
    val SingleThreadTimer = Value(1, "SINGLETHREADTIMER")
    val MultiThreadTimer = Value(2, "MULTITHREADTIMER")

    def apply(name: String): TimerType = {
      this.apply(name.toUpperCase())
    }

    def getName(timerType: TimerType): String = {
      timerType match {
        case SimpleTimer => "SIMPLETIMER"
        case SingleThreadTimer => "SINGLETHREADTIMER"
        case MultiThreadTimer => "MULTITHREADTIMER"
        case _ => throw new IllegalArgumentException(s"argument must be in {SIMPLETIMER, SINGLETHREADTIMER, MULTITHREADTIMER}")
      }
    }

  }

}
