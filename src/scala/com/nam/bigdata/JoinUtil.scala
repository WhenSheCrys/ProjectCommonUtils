package scala.com.nam.bigdata

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.{HashPartitioner, Partitioner}
import org.apache.spark.sql.catalyst.plans._
import org.apache.spark.sql._
import org.apache.spark.sql.types.StructType

/**
  * Created by Namhwik on 2018/6/29.
  */
object JoinUtil {
  val defaultPartitioner: HashPartitioner = new HashPartitioner(100)
  private final val APPEND_FLAG = "_JOINFLAG"

  def join(leftData: DataFrame, rightData: DataFrame, joinExprs: Column, joinType: JoinType): DataFrame = {
    println(joinType.sql)
    val children = joinExprs.expr.children.map(_.toString())
    require(children.lengthCompare(3) < 0 && children.nonEmpty)
    val left: DataFrame = leftData
    var right: DataFrame = null
    var temporaryColumnName: String = null

    val joinColumns = children.size match {
      case 1 =>
        val colName = children.head.split("#").head
        temporaryColumnName = colName + APPEND_FLAG
        right = rightData.withColumnRenamed(colName, temporaryColumnName)
        Array(colName, temporaryColumnName)
      case 2 =>
        right = rightData
        val colNames = children.map(_.split("#").head)
        val allColumns = left.columns ++ right.columns
        val selectColumns = allColumns.distinct
        require(allColumns.length == selectColumns.length, "参与Join的DataFrame不允许存在除连接主键列以外相同的列")
        if (left.columns.contains(colNames.head)) Array(colNames.head, colNames(1)) else Array(colNames(1), colNames.head)
    }


    val leftColsSize = left.columns.length
    val rightColsSize = right.columns.length
    val schema = StructType(left.schema.toArray ++ right.schema.toArray)

    val rdds =
      Array(left, right).zipWithIndex.par
        .map(x => df2PairByKeys(x._1, Array(joinColumns(x._2)), defaultPartitioner))

    val dataFrameBuilder = (rdd: RDD[Row]) => {
      val sparkSession = SparkSessionBuilder.spark
      val dataFrame = sparkSession.createDataFrame(rdd, schema)
      if (temporaryColumnName == null) dataFrame else dataFrame.drop(temporaryColumnName)
    }


    val joinedRdd = joinType match {
      case Inner =>
        rdds.head.join(rdds(1), defaultPartitioner)
      case LeftOuter =>
        rdds.head.leftOuterJoin(rdds(1), defaultPartitioner)
      case RightOuter =>
        rdds.head.rightOuterJoin(rdds(1), defaultPartitioner)
      case FullOuter =>
        rdds.head.fullOuterJoin(rdds(1), defaultPartitioner)
    }

    dataFrameBuilder(joinedRdd.mapPartitions(iteratorMapper(_, leftColsSize, rightColsSize)))

  }

  /**
    * 构造RDD
    *
    * @param data DataFrame
    * @param keys 主键列
    * @return
    */
  def df2PairByKeys(data: DataFrame, keys: Array[String], partitioner: Partitioner): RDD[(String, Row)] = {
    val schema: StructType = data.schema
    val encoder: Encoder[(String, Row)] = Encoders.tuple(
      Encoders.STRING,
      RowEncoder(schema)
    )
    val rowValueAdapter = RowValueAdapter(schema)
    data.mapPartitions(ps => {
      val valueAdapter = rowValueAdapter
      ps.map(r => (keys.map(valueAdapter.valueOf(_, r)).mkString(""), r))
    })(encoder).rdd
      .repartitionAndSortWithinPartitions(partitioner)

  }

  def emptyRowArr(row: Any, size: Int): Seq[Any] =
    row match {
      case option: Option[Row] =>
        if (option.isEmpty)
          (0 until size).map(_ => null)
        else
          option.get.toSeq
      case r: Row => r.toSeq
    }

  def iteratorMapper(iterator: Iterator[(String, (Any, Any))], leftSize: Int, rightSize: Int): Iterator[Row] =
    iterator.map(x => Row(emptyRowArr(x._2._1, leftSize) ++ emptyRowArr(x._2._2, rightSize): _*))


}
