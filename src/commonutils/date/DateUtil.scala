package commonutils.date

import java.util.{Calendar, Date}

import commonutils.date.DateUtil.TimeUnit.TimeUnit

object DateUtil {

  def dateTransfer(in: Date, mount: Int, unit: TimeUnit): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(in)
    val u = unit match {
      case TimeUnit.DAYS => Calendar.DAY_OF_YEAR
      case TimeUnit.HOURS => Calendar.HOUR_OF_DAY
      case TimeUnit.YEARS => Calendar.YEAR
      case TimeUnit.WEEKS => Calendar.WEEK_OF_YEAR
      case TimeUnit.MINUTES => Calendar.MINUTE
      case TimeUnit.SECONDS => Calendar.SECOND
      case TimeUnit.MILLINSECONDS => Calendar.MILLISECOND
      case _ => throw new RuntimeException(s"TimeUnit:$unit not match")
    }
    cal.set(u, cal.get(u) + mount)
    cal.getTime
  }

  object TimeUnit extends Enumeration {

    type TimeUnit = TimeUnit.Value

    val MILLINSECONDS = Value(0, "MILLINSECONDS")
    val SECONDS = Value(1, "SECONDS")
    val MINUTES = Value(2, "MINUTES")
    val HOURS = Value(3, "HOURS")
    val DAYS = Value(4, "DAYS")
    val WEEKS = Value(5, "WEEKS")
    val MONTHS = Value(6, "MONTHS")
    val YEARS = Value(7, "YEARS")
  }

}


