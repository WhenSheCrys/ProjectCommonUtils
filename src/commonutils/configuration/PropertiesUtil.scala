package commonutils.configuration

import java.io.{File, FileInputStream}
import java.util.Properties

import org.json.JSONObject

import scala.collection.JavaConverters._

private[configuration] class PropertiesUtil(private val fileName: String = "config.properties") extends ConfigurationUtil {

  private var properties = new Properties()

  /**
    * 获取配置信息
    *
    * @param name
    * @return
    */
  override def getProperty(name: String): String = {
    val res = properties.getProperty(name, null)
    if (null != res) {
      res
    } else {
      throw new RuntimeException(s"Cant not find a value by name '$name' in $fileName!")
    }
  }

  /**
    * 如果获取不到则取默认值
    *
    * @param name
    * @param default
    * @return
    */
  override def getOrDefault(name: String, default: String): String = {
    properties.getProperty(name, default)
  }

  /**
    * 如果获取不到则取默认值
    *
    * @param name
    * @param other
    * @return
    */
  def getOrElse(name: String, other: Any): Any = {
    Option(properties.get(name)).getOrElse(other)
  }

  override def load(fileName: String): this.type = {
    getProperties(fileName)
    this
  }

  /**
    * 获取配置的全部信息
    *
    * @return
    */
  private def getProperties(fileName: String): Properties = {
    def loadProperties(p: Properties): Properties = {
      p.load(this.getClass.getClassLoader.getResourceAsStream(fileName))
      p
    }

    if (null == properties) {
      properties = new Properties()
      loadProperties(properties)
    } else if (properties.isEmpty) {
      loadProperties(properties)
    } else {
      properties
    }

  }

  /**
    * 转换为Map[String, String]
    *
    * @return
    */
  override def toMap(): Map[String, String] = {
    properties.asScala.toMap
  }

  /**
    * 从文件加载
    *
    * @param file
    * @return
    */
  override def fromFile(file: File): this.type = {
    val p = new Properties()
    val instream = new FileInputStream(file)
    p.load(instream)
    properties = p
    this

  }

  /**
    * 转换为Json
    *
    * @return
    */
  override def toJson(): String = {
    val json = new JSONObject()
    properties.asScala.foreach { p =>
      json.put(p._1, p._2)
    }
    json.toString()
  }

  /**
    * 是否包含
    *
    * @param name
    * @return
    */
  override def contains(name: String): Boolean = {
    properties.contains(name)
  }
}
