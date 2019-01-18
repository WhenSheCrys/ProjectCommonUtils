package scala.com.nam.commonutils.configuration

import java.util.Properties

object ConfigUtil {
  private val fileName = "config.properties"
  private var properties = new Properties()

  /**
    * 获取配置信息的stripesize
    *
    * @param name
    * @return
    */
  def getSripeSize(name: String = "orc.stripe.size"): Long = {
    val sizeString = getConfig(name).toUpperCase
    val endArr = Array("B", "KB", "MB", "GB", "TB", "PB", "K", "M", "G", "T", "P")
    val size = sizeString.map(_.toInt)
      .filter(x => x >= 48 && x <= 57)
      .map(_.toChar).mkString("")
    val unit = sizeString.diff(size)
    require(endArr.contains(unit), "Unit must in " + endArr.mkString(","))
    val res = if (unit.startsWith("B")) {
      size.toLong
    } else if (unit.startsWith("K")) {
      size.toLong * 1024
    } else if (unit.startsWith("M")) {
      size.toLong * 1024 * 1024
    } else if (unit.startsWith("G")) {
      size.toLong * 1024 * 1024 * 1024
    } else if (unit.startsWith("T")) {
      size.toLong * 1024 * 1024 * 1024 * 1024
    } else if (unit.startsWith("P")) {
      size.toLong * 1024 * 1024 * 1024 * 1024 * 1024
    } else {
      size.toLong
    }
    res
  }

  /**
    * 获取配置信息
    *
    * @param name
    * @return
    */
  def getConfig(name: String): String = {
    properties = getProperties()
    val res = properties.getProperty(name, null)
    if (null != res) {
      res
    } else {
      throw new RuntimeException(s"Cant not find a value by name '$name' in $fileName!")
    }
  }

  /**
    * 获取配置的全部信息
    *
    * @return
    */
  private def getProperties(): Properties = {
    def load(p: Properties): Properties = {
      p.load(this.getClass.getClassLoader.getResourceAsStream(fileName))
      p
    }

    if (null == properties) {
      properties = new Properties()
      load(properties)
    } else if (properties.isEmpty) {
      load(properties)
    } else {
      properties
    }

  }
}
