package commonutils.date

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, TimeZone}

import commonutils.date.CommonType.CommonType
import commonutils.date.StatisticsType.StatisticsType
import commonutils.date.TimeUnit.TimeUnit
import org.apache.log4j.Logger

object DateUtil extends Serializable {

  val logger = Logger.getLogger(this.getClass)

  /**
    * 获取日期的统计信息
    *
    * @param date
    * @param dateType
    * @return
    */
  def get(date: Date, dateType: StatisticsType): Long = {
    val cal = Calendar.getInstance()
    cal.setTime(date)

    def dayOfQuarter = {
      cal.setTime(date)
      val month = cal.get(Calendar.MONTH) + 1
      val firstMonthOfQuater = if (month >= 1 && month <= 3) {
        0
      } else if (month >= 4 && month <= 6) {
        3
      } else if (month >= 7 && month <= 9) {
        6
      } else {
        9
      }
      val cal2 = Calendar.getInstance()
      cal2.setTime(date)
      cal2.set(Calendar.MONTH, firstMonthOfQuater)
      cal.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR)
    }

    def weekOfQuarter = {
      val month = cal.get(Calendar.MONTH) + 1
      val firstMonthOfQuater = if (month >= 1 && month <= 3) {
        0
      } else if (month >= 4 && month <= 6) {
        3
      } else if (month >= 7 && month <= 9) {
        6
      } else {
        9
      }
      val cal2 = Calendar.getInstance()
      cal2.setTime(date)
      cal2.set(Calendar.MONTH, firstMonthOfQuater)
      cal.get(Calendar.WEEK_OF_YEAR) - cal2.get(Calendar.WEEK_OF_YEAR)
    }

    def monthOfQuarter = {
      val month = cal.get(Calendar.MONTH) + 1
      val firstMonthOfQuater = if (month >= 1 && month <= 3) {
        0
      } else if (month >= 4 && month <= 6) {
        3
      } else if (month >= 7 && month <= 9) {
        6
      } else {
        9
      }
      val cal2 = Calendar.getInstance()
      cal2.setTime(date)
      cal2.set(Calendar.MONTH, firstMonthOfQuater)
      cal.get(Calendar.MONTH) - cal2.get(Calendar.MONTH)
    }

    def quarterOfYear = {
      val month = cal.get(Calendar.MONTH) + 1
      if (month >= 1 && month <= 3) {
        1
      } else if (month >= 4 && month <= 6) {
        2
      } else if (month >= 7 && month <= 9) {
        3
      } else {
        4
      }
    }

    def totalDaysOfQuarter = {
      val start = this.get(date, CommonType.FIRST_DAY_OF_QUARTER)
      val end = this.get(date, CommonType.LAST_DAY_OF_QUARTER)
      val cal1 = Calendar.getInstance()
      cal1.setTime(start)
      val cal2 = Calendar.getInstance()
      cal2.setTime(end)
      cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR)
    }

    dateType match {
      case StatisticsType.MILLISECOND => cal.get(Calendar.MILLISECOND)
      case StatisticsType.SECOND => cal.get(Calendar.SECOND)
      case StatisticsType.MINUTE => cal.get(Calendar.MINUTE)
      case StatisticsType.HOUR => cal.get(Calendar.HOUR_OF_DAY)
      case StatisticsType.DAY_OF_WEEK => cal.get(Calendar.DAY_OF_WEEK)
      case StatisticsType.DAY_OF_MONTH => cal.get(Calendar.DAY_OF_MONTH)
      case StatisticsType.DAY_OF_QUARTER => dayOfQuarter
      case StatisticsType.DAY_OF_YEAR => cal.get(Calendar.DAY_OF_YEAR)
      case StatisticsType.WEEK_OF_MONTH => cal.get(Calendar.WEEK_OF_MONTH)
      case StatisticsType.WEEK_OF_QUARTER => weekOfQuarter
      case StatisticsType.WEEK_OF_YEAR => cal.get(Calendar.WEEK_OF_YEAR)
      case StatisticsType.MONTH_OF_QUARTER => monthOfQuarter
      case StatisticsType.MONTH_OF_YEAR => cal.get(Calendar.YEAR)
      case StatisticsType.QUARTER_OF_YEAR => quarterOfYear
      case StatisticsType.TOTAL_DAYS_OF_MONTH => cal.getActualMaximum(Calendar.DAY_OF_MONTH)
      case StatisticsType.TOTAL_DAYS_OF_QUARTER => totalDaysOfQuarter
      case StatisticsType.TOTAL_DAYS_OF_YEAR => cal.getActualMaximum(Calendar.DAY_OF_YEAR)
      case _ => throw new UnsupportedOperationException("Operation not supported now!")
    }
  }

  /**
    * 获取日期的基本信息
    *
    * @param date
    * @param dateType
    * @return
    */
  def get(date: Date, dateType: CommonType): Date = {

    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    def firstDayOfWeek = {
      cal.set(Calendar.DAY_OF_WEEK, 1)
      cal.getTime
    }

    def firstDayOfMonth = {
      cal.set(Calendar.DAY_OF_MONTH, 1)
      cal.getTime
    }

    def firstDayOfQuarter = {
      val month = cal.get(Calendar.MONTH) + 1
      val firstMonthOfQuater = if (month >= 1 && month <= 3) {
        0
      } else if (month >= 4 && month <= 6) {
        3
      } else if (month >= 7 && month <= 9) {
        6
      } else {
        9
      }
      cal.set(Calendar.MONTH, firstMonthOfQuater)
      cal.set(Calendar.DAY_OF_MONTH, 1)
      cal.getTime
    }

    def firstDayOfYear = {
      cal.set(Calendar.DAY_OF_YEAR, 1)
      cal.getTime
    }

    def endTimeOfDay = {
      cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY))
      cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE))
      cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND))
      cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND))
      cal.getTime
    }

    def endTimeOfWeek = {
      cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK))
      endTimeOfDay
    }

    def endTimeOfMonth = {
      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      endTimeOfDay
    }

    def endTimeOfQuarter = {
      val month = cal.get(Calendar.MONTH) + 1
      val lastMonthOfQuater = if (month >= 1 && month <= 3) {
        2
      } else if (month >= 4 && month <= 6) {
        5
      } else if (month >= 7 && month <= 9) {
        8
      } else {
        11
      }
      cal.set(Calendar.MONTH, lastMonthOfQuater)
      endTimeOfMonth
    }

    def endTimeOfYear = {
      cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
      endTimeOfDay
    }

    dateType match {
      case CommonType.FIRST_DAY_OF_WEEK => firstDayOfWeek
      case CommonType.FIRST_DAY_OF_MONTH => firstDayOfMonth
      case CommonType.FIRST_DAY_OF_QUARTER => firstDayOfQuarter
      case CommonType.FIRST_DAY_OF_YEAR => firstDayOfYear
      case CommonType.START_TIME_OF_DAY => cal.getTime
      case CommonType.START_TIME_OF_MONTH => firstDayOfMonth
      case CommonType.START_TIME_OF_WEEK => firstDayOfWeek
      case CommonType.START_TIME_OF_QUARTER => firstDayOfQuarter
      case CommonType.START_TIME_OF_YEAR => firstDayOfYear
      case CommonType.END_TIME_OF_DAY => endTimeOfDay
      case CommonType.END_TIME_OF_WEEK => endTimeOfWeek
      case CommonType.END_TIME_OF_MONTH => endTimeOfMonth
      case CommonType.END_TIME_OF_QUARTER => endTimeOfQuarter
      case CommonType.END_TIME_OF_YEAR => endTimeOfYear
      case _ => throw new UnsupportedOperationException("Operation not supported now!")
    }
  }

  /**
    * 获取日期跨度
    *
    * @param startDate
    * @param endDate
    * @param timeUnit
    * @return
    */
  def rangeInLong(startDate: Date, endDate: Date, timeUnit: TimeUnit): Long = {
    rangeInDouble(startDate, endDate, timeUnit).toLong
  }

  /**
    * 获取日期跨度
    *
    * @param startDate
    * @param endDate
    * @param timeUnit
    * @return
    */
  def rangeInDouble(startDate: Date, endDate: Date, timeUnit: TimeUnit): Double = {
    if (endDate.before(startDate)) {
      logger.warn("END_DATE is earlier than START_DATE! result is negative!")
    }
    val diff = endDate.getTime.toDouble - startDate.getTime.toDouble
    timeUnit match {
      case TimeUnit.MILLISECONDS => diff
      case TimeUnit.SECONDS => diff / 1000D
      case TimeUnit.MINUTES => diff / (1000D * 60)
      case TimeUnit.HOURS => diff / (1000D * 60 * 60)
      case TimeUnit.DAYS => diff / (1000D * 60 * 60 * 24)
      case TimeUnit.WEEKS => diff / (1000D * 60 * 60 * 24 * 7)
      case TimeUnit.MONTHS =>
        logger.warn("month is not fixed in a year, so we set it to 30, that the result may not be exact.")
        diff / (1000D * 60 * 60 * 24 * 30)
      case TimeUnit.YEARS =>
        logger.warn("days is not fixed in a year, so we set it to 365, that the result may not be exact.")
        diff / (1000D * 60 * 60 * 24 * 365)
    }
  }

  /**
    * 是否是闰年
    *
    * @param date
    * @return
    */
  def isLeapYear(date: Date): Boolean = {
    val cal = Calendar.getInstance()
    cal.setTime(date)
    val year = cal.get(Calendar.YEAR)
    year % 4 == 0 && year % 100 != 0 || year % 400 == 0
  }

  /**
    * 字符串转换为时间
    *
    * @param dt      字符串时间
    * @param pattern 格式
    * @return
    */
  def parseDate(dt: String, pattern: String): Date = {
    new SimpleDateFormat(pattern).parse(dt)
  }

  /**
    * 时间转换
    *
    * @param in    要转换的时间
    * @param mount 数量
    * @param unit  时间单位
    * @return
    */
  def dateTransfer(in: Date, mount: Int, unit: TimeUnit): Date = synchronized {
    val cal = Calendar.getInstance()
    cal.setTime(in)
    val u = unit match {
      case TimeUnit.DAYS => Calendar.DAY_OF_YEAR
      case TimeUnit.HOURS => Calendar.HOUR_OF_DAY
      case TimeUnit.YEARS => Calendar.YEAR
      case TimeUnit.WEEKS => Calendar.WEEK_OF_YEAR
      case TimeUnit.MINUTES => Calendar.MINUTE
      case TimeUnit.SECONDS => Calendar.SECOND
      case TimeUnit.MILLISECONDS => Calendar.MILLISECOND
      case _ => throw new RuntimeException(s"TimeUnit:$unit not match")
    }
    cal.set(u, cal.get(u) + mount)
    cal.getTime
  }

  def before(millisecond1: Long, millisecond2: Long): Boolean = {
    millisecond1 < millisecond2
  }

  def before(calendar1: Calendar, calendar2: Calendar): Boolean = {
    calendar1.before(calendar2)
  }

  def before(timestamp1: Timestamp, timestamp2: Timestamp): Boolean = {
    timestamp1.before(timestamp2)
  }

  def before(date1: Date, date2: Date): Boolean = {
    date1.before(date2)
  }

  def after(millisecond1: Long, millisecond2: Long): Boolean = {
    millisecond1 > millisecond2
  }

  def after(calendar1: Calendar, calendar2: Calendar): Boolean = {
    calendar1.after(calendar2)
  }

  def after(timestamp1: Timestamp, timestamp2: Timestamp): Boolean = {
    timestamp1.after(timestamp2)
  }

  def after(date1: Date, date2: Date): Boolean = {
    date1.after(date2)
  }

  def compare(millisecond1: Long, millisecond2: Long): Int = {
    millisecond1.compareTo(millisecond2)
  }

  def compare(date1: Date, date2: Date): Int = {
    date1.compareTo(date2)
  }

  def compare(timestamp1: Timestamp, timestamp2: Timestamp): Int = {
    timestamp1.compareTo(timestamp2)
  }

  def compare(calendar1: Calendar, calendar2: Calendar): Int = {
    calendar1.compareTo(calendar2)
  }

  def toDate(calendar: Calendar): Date = {
    calendar.getTime
  }

  def toTimestamp(date: Date): Timestamp = {
    new Timestamp(date.getTime)
  }

  def toTimestamp(cal: Calendar): Timestamp = {
    new Timestamp(cal.getTimeInMillis)
  }

  def toTimestamp(millisecond: Long): Timestamp = {
    new Timestamp(millisecond)
  }

  def toMilliseconds(cal: Calendar): Long = {
    cal.getTimeInMillis
  }

  def toMilliseconds(date: Date): Long = {
    date.getTime
  }

  def toMilliseconds(ts: Timestamp): Long = {
    ts.getTime
  }

  def toCalendar(date: Date): Calendar = {
    val c = Calendar.getInstance
    c.setTime(date)
    c
  }

  def toCalendar(date: Date, tz: TimeZone): Calendar = {
    val c = Calendar.getInstance(tz)
    c.setTime(date)
    c
  }

  def toCalendar(ts: Timestamp): Calendar = {
    val c = Calendar.getInstance
    c.setTime(toDate(ts))
    c
  }

  def toCalendar(ts: Timestamp, tz: TimeZone): Calendar = {
    val c = Calendar.getInstance(tz)
    c.setTime(toDate(ts))
    c
  }

  def toDate(ts: Timestamp): Date = {
    toDate(ts.getTime)
  }

  def toCalendar(millisecond: Long): Calendar = {
    val c = Calendar.getInstance
    c.setTime(toDate(millisecond))
    c
  }

  def toCalendar(millisecond: Long, tz: TimeZone): Calendar = {
    val c = Calendar.getInstance(tz)
    c.setTime(toDate(millisecond))
    c
  }

  def toDate(unixTime: Long): Date = {
    new Date(unixTime)
  }

  def isSameDay(date1: Date, date2: Date): Boolean = {
    if (date1 == null || date2 == null) throw new IllegalArgumentException("The date must not be null")
    val cal1 = Calendar.getInstance
    cal1.setTime(date1)
    val cal2 = Calendar.getInstance
    cal2.setTime(date2)
    isSameDay(cal1, cal2)
  }

  def isSameDay(cal1: Calendar, cal2: Calendar): Boolean = {
    if (cal1 == null || cal2 == null) throw new IllegalArgumentException("The date must not be null")
    cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
  }

  def isSameLocalTime(cal1: Calendar, cal2: Calendar): Boolean = {
    if (cal1 == null || cal2 == null) throw new IllegalArgumentException("The date must not be null")
    cal1.get(Calendar.MILLISECOND) == cal2.get(Calendar.MILLISECOND) && cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND) && cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE) && cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && (cal1.getClass eq cal2.getClass)
  }

}
