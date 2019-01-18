package scala.com.nam.bigdata

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructField, StructType}


/**
  * Created by Namhwik on 2018/6/29.
  */
case class RowValueAdapter(schema: StructType) {
  protected val schemaMap: Map[String, String] = schema.map(x => (x.name, x.dataType.typeName)).toMap

  def valueOf(name: String,row: Row): String = {
    schemaMap(name).toLowerCase match {
      case "string" => row.getAs[String](name)
      case "double" =>row.getAs[Double](name).toString
      case "int" =>row.getAs[Int](name).toString
      case "decimal" =>row.getAs[java.math.BigDecimal](name).toString
      case "Long" =>row.getAs[Long](name).toString
      case _ =>
        if(schemaMap(name).toLowerCase.startsWith("decimal"))
          row.getAs[java.math.BigDecimal](name).toString
        else
          throw new RuntimeException(s"Can Not Match Type ${schemaMap(name).toLowerCase} ...")
    }
  }
}
