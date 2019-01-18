package com.nam.commonutils.udfUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, UUID}

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

/**
  * Created with IntelliJ IDEA.
  * User: jack
  * Date: 2018/5/30
  * Time: 9:10
  * Description: 本类存放通用UDF
  */
object CommonUdf {

  /**
    * 获取当前日期，不含时分秒
    *
    * @return
    */
  def getCurrentDate(): String = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    sdf.format(new Date())
  }

  /**
    * 获取当前日期包含时分秒
    *
    * @return
    */
  def getCurrentTime(): String = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.format(new Date())
  }

  /**
    * 拼接UUID
    */
  val appendUUID = udf { () =>
    UUID.randomUUID().toString.replaceAll("-", "").toUpperCase()
  }

  /**
    * 拼接当前日期
    */
  val appendCurrentDay = udf { (date: String) =>
    Timestamp.valueOf(date + " 00:00:00")
  }

  /**
    * 拼接当前时间
    */
  val appendCurrentTime = udf { (time: String) =>
    Timestamp.valueOf(time)
  }

  /**
    * 转换成Int
    */
  val deal2Double0 = udf { (value: Object) =>
    try {
      val decial = new java.math.BigDecimal(String.valueOf(value)).
        setScale(0, java.math.BigDecimal.ROUND_HALF_EVEN)
      decial.doubleValue()
    } catch {
      case ex: Exception => {
        0.0
      }
    }
  }

  /**
    * 转换成精度为2为的Float
    */
  val deal2Double2 = udf { (value: Object) =>
    try {
      val decial = new java.math.BigDecimal(String.valueOf(value)).
        setScale(2, java.math.BigDecimal.ROUND_HALF_EVEN)
      decial.doubleValue()
    } catch {
      case ex: Exception => {
        0.0
      }
    }
  }

  /**
    * 获取昨天的日期
    *
    * @return
    */
  def getYesterday():String ={
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.DATE,-1)
    val yesterday = dateFormat.format(cal.getTime)
    yesterday
  }

  val computeDuration: UserDefinedFunction = udf((start: Timestamp, end: Timestamp) => {
    val duration = (end.getTime - start.getTime) / 86400000.0

    if (duration < 60) {
      if (duration < 30)
        duration.toString
      else
        "30"
    } else "60"
  })

  val fixGDDWBM: UserDefinedFunction = udf((gddwbm: String) => "0" + gddwbm)

  val appendFlag: UserDefinedFunction = udf((v: java.math.BigDecimal) => if (v.doubleValue() > 0) 1L else 0L)

}
