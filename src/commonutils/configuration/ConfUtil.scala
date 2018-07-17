package commonutils.configuration

object ConfUtil {

  def builder: UtilBuilder = {
    new UtilBuilder
  }

  class UtilBuilder() {
    private[this] var tp: ConfType.Value = _
    private[this] var fileName: String = _

    def setFile(fileName: String): this.type = {
      this.fileName = fileName
      this
    }

    def propertiesUtil(tp: ConfType.Value): this.type = {
      this.tp = tp
      this
    }

    def finish(): ConfigurationUtil = {
      if (null == tp) {
        throw new IllegalArgumentException("Type Name must be specified!")
      }
      tp match {
        case ConfType.Properties => new PropertiesUtil(fileName)
        case _ => throw new IllegalArgumentException(s"Unsupported Type:${tp.toString}")
      }
    }
  }

  object ConfType extends Enumeration {
    val Properties, Ini, json = Value
  }

}
