package scala.org.zodiac.spark

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ArrayBuffer

object BroadcastUtil {

  def apply(spark: SparkSession): BroadcastBuilder = {
    builder(spark)
  }

  def builder(spark: SparkSession = SparkSession.builder().getOrCreate()): BroadcastBuilder = {
    new BroadcastBuilder(spark)
  }

  def builder(): BroadcastBuilder = {
    new BroadcastBuilder()
  }

  def apply(spark: SparkSession, broadcastList: ArrayBuffer[Any]): BroadcastBuilder = {
    val broadcastBuilder = builder(spark)
    broadcastBuilder.add(broadcastList: _*)
  }

  private[BroadcastUtil] class BroadcastBuilder(private[this] val spark: SparkSession) {

    private[BroadcastUtil] final val broadcastList = ArrayBuffer[Any]()

    def this() = this(SparkSession.builder().getOrCreate())

    def add(broadcastVal: Any*): BroadcastBuilder = {
      broadcastList.append(broadcastVal: _*)
      this
    }

    def remove(broadcastVal: Any): BroadcastBuilder = {
      broadcastList.remove(broadcastList.indexOf(broadcastVal))
      this
    }

    def broadcast(): ArrayBuffer[Broadcast[Any]] = {
      broadcastList.map(spark.sparkContext.broadcast)
    }

  }

}
