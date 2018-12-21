package commonutils.spark

import org.apache.spark.sql.functions._
import commonutils.text.StringUtil
import org.apache.spark.sql.expressions.UserDefinedFunction

object UserDefinedFunctions {

  val string2Double: UserDefinedFunction = udf { input: String =>
    if (StringUtil.isBlank(input)) {
      0D
    } else {
      try {
        input.toDouble
      } catch {
        case _: Exception =>
          0D
      }
    }
  }

  val stringNotNull: UserDefinedFunction = udf { input: String =>
    if (null == input) {
      ""
    } else {
      input
    }
  }



}
