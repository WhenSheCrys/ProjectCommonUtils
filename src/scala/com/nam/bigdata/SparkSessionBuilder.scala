package scala.com.nam.bigdata
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * Created by Namhwik on 2018/3/1.
  */
object SparkSessionBuilder {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  private lazy val sparkBuilder = SparkSession
    .builder()
    .config("spark.scheduler.mode", "FAIR")
  lazy val spark: SparkSession = if (scala.sys.props.get("user.name").head.equals("namhwik")) {
    System.setProperty("HADOOP_USER_NAME", "root")
    System.setProperty("user.name", "root")
    System.setProperty("hadoop.home.dir", "D:\\DevelopmentEnvironment\\hadoop-2.7.1")
    System.setProperty("spark.sql.warehouse.dir", "/")
    sparkBuilder.appName("NAMHWIK").master("local[*]").getOrCreate()
  }
  else
    sparkBuilder.appName("NAMHWIK").getOrCreate()

}
