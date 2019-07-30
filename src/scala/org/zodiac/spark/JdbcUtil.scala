package scala.org.zodiac.spark

import java.util.Properties

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.{DataFrame, SparkSession}

object JdbcUtil {

  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder().appName("test").master("local").getOrCreate()
    jdbcReader().setDbType(DbType.Oracle).setDbHost("172.20.36.25").setDbPort(1521).setDbName("hydb")
      .setDbUser("PMS_SZ").setDbPassword("PMS_SZ").setDriver("oracle.jdbc.driver.OracleDriver")
      .setTableName("(SELECT * FROM HS_JLDXX WHERE ROWNUM <= 100)").read.show(false)
  }

  def jdbcReader(): JdbcReader = new JdbcReader()

  class JdbcReader {

    private[this] var url: String = _
    private[this] var host: String = _
    private[this] var port: Int = _
    private[this] var dbName: String = _
    private[this] var user: String = _
    private[this] var password: String = _
    private[this] var driver: String = _
    private[this] var table: String = _
    private[this] var dbType: String = _

    def setDbType(dbType: String): this.type = {
      this.dbType = dbType
      this
    }

    def setDbHost(host: String): this.type = {
      this.host = host
      this
    }

    def setDbPort(port: Int): this.type = {
      this.port = port
      this
    }

    def setDbName(name: String): this.type = {
      this.dbName = name
      this
    }

    def setDbUser(user: String): this.type = {
      this.user = user
      this
    }

    def setDbPassword(password: String): this.type = {
      this.password = password
      this
    }

    def setTableName(table: String): this.type = {
      this.table = table
      this
    }

    def setDriver(driver: String): this.type = {
      this.driver = driver
      this
    }

    def read: DataFrame = {
      build()
      val properties = new Properties()
      properties.put("user", user)
      properties.put("password", password)
      properties.put("driver", driver)
      SparkSession.builder().getOrCreate().read.jdbc(url, table, properties)
    }

    private[this] def build(): Unit = {
      implicit val func: String => Boolean = (x: String) => StringUtils.isNoneEmpty(x) && StringUtils.isNoneBlank(x)
      implicit val func2: Int => Boolean = (x: Int) => x > 0 && x <= Short.MaxValue
      validateString(host, func, "host must not be null or empty!")
      validateInt(port, func2, s"port should between 0 and ${Short.MaxValue}")
      validateString(dbName, func, "dbName must not be null or empty!")
      validateString(user, func, "user must not be null! or empty")
      validateString(password, func, "password must not be null or empty!")
      validateString(driver, func, "driver must not be null or empty!")
      assert(DbType.validate(dbType), s"dbType should be one of ${DbType.toString}, but $dbType found!")
      if (dbType == DbType.Oracle) {
        url = s"jdbc:oracle:thin:@$host:$port/$dbName"
      } else if (dbType == DbType.MySql) {

      }
    }

    private[this] def validateString(value: String, func: String => Boolean, describe: String): Unit = {
      assert(func(value), describe)
    }

    private[this] def validateInt(value: Int, func: Int => Boolean, describe: String): Unit = {
      assert(func(value), describe)
    }

  }

  object DbType {
    val Oracle: String = "oracle"
    val MySql: String = "mysql"

    private[this] val arr = Array(Oracle, MySql)

    def validate(dbType: String): Boolean = {
      arr.contains(dbType)
    }

    override def toString: String = {
      arr.mkString("[", ", ", "]")
    }

  }

}
