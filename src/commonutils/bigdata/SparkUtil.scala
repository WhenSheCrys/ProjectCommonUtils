package com.haiyisoft.utils

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Logger, Level}

object SparkUtil {
  def getSparkSession(isLocal: Boolean = false, conf: Map[String, String] = Map()): SparkSession = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    val sparkConf = new SparkConf()
    if (null != sparkConf && conf.nonEmpty)
      conf.foreach(x => sparkConf.set(x._1, x._2))
    val s = SparkSession.builder().appName("Spark")
    val spark = if (isLocal) {
      s.master("local")
    } else {
      s
    }.config(sparkConf)
    spark.getOrCreate()
  }
}
