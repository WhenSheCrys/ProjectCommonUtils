package commonutils.date.util

package object scala {

  /** **************************** 时间单位枚举类 ******************************/

  object TimeUnit extends Enumeration {

    type TimeUnit = TimeUnit.Value

    val MILLISECONDS = Value(0, "MILLISECONDS")
    val SECONDS = Value(1, "SECONDS")
    val MINUTES = Value(2, "MINUTES")
    val HOURS = Value(3, "HOURS")
    val DAYS = Value(4, "DAYS")
    val WEEKS = Value(5, "WEEKS")
    val MONTHS = Value(6, "MONTHS")
    val YEARS = Value(7, "YEARS")
  }

  object CommonType extends Enumeration {

    type CommonType = CommonType.Value

    val
    FIRST_DAY_OF_WEEK, FIRST_DAY_OF_MONTH, FIRST_DAY_OF_QUARTER, FIRST_DAY_OF_YEAR,
    LAST_DAY_OF_WEEK, LAST_DAY_OF_MONTH, LAST_DAY_OF_QUARTER, LAST_DAY_OF_YEAR,
    START_TIME_OF_DAY, START_TIME_OF_WEEK, START_TIME_OF_MONTH, START_TIME_OF_QUARTER, START_TIME_OF_YEAR,
    END_TIME_OF_DAY, END_TIME_OF_WEEK, END_TIME_OF_MONTH, END_TIME_OF_QUARTER, END_TIME_OF_YEAR = Value

  }

  object StatisticsType extends Enumeration {

    type StatisticsType = StatisticsType.Value

    val TOTAL_DAYS_OF_MONTH, TOTAL_DAYS_OF_QUARTER, TOTAL_DAYS_OF_YEAR,
    DAY_OF_WEEK, DAY_OF_MONTH, DAY_OF_YEAR, DAY_OF_QUARTER,
    WEEK_OF_MONTH, WEEK_OF_QUARTER, WEEK_OF_YEAR,
    MONTH_OF_QUARTER, MONTH_OF_YEAR,
    QUARTER_OF_YEAR,
    YEAR, HOUR, MINUTE, SECOND, MILLISECOND = Value
  }


}
