package commonutils.configuration

import java.io.File

trait ConfigurationUtil extends Serializable {

  def load(fileName: String): ConfigurationUtil

  def getProperty(name: String): String

  def getOrDefault(name: String, default: String): String

  def getOrElse(name: String, other: Any): Any

  def toMap(): Map[String, String]

  def fromFile(file: File): ConfigurationUtil

  def toJson(): String

  def contains(name: String): Boolean

}
