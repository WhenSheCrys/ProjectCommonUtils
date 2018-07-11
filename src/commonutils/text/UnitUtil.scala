package commonutils.text

import scala.util.matching.Regex

object UnitUtil {

  private final val prefix = Array("K", "M", "G", "T", "P", "E", "Z", "Y").zipWithIndex.toMap

  object ByteUtil {

    def fromText(text: String): Long = {
      val reg: Regex = "^(\\d+\\.?\\d+?)([KkMmGgTtPpEeZzYy]?)([Bb]?)$".r
      val matches = reg.findFirstIn(text).getOrElse("")

      if (matches == "") {
        throw new IllegalArgumentException("Can't find matches in input String!")
      }

      val num_reg = "\\d+\\.?\\d+?".r
      val unit1_reg = "([KkMmGgTtPpEeZzYy])".r
      val unit2_reg = "([Bb])".r

      val num = num_reg.findFirstIn(matches).get.toFloat
      val unit1 = unit1_reg.findFirstIn(matches).get
      val unit2 = unit2_reg.findFirstIn(matches).get

      val res = if (prefix.contains(unit1)) {
        num * (1L << (prefix(unit1) + 1) * 10L)
      } else {
        num
      }
      val result =
        if (unit2 == "b") {
          res / 8
        } else {
          res
        }
      result.toLong
    }

    def toString(in: Long): String = {
      val r = in
      val p = prefix.map(x => (1L << (x._2 + 1) * 10L, x._1)).toArray
      p.foreach { x =>
        if (r / x._1 < 1024) {
          return r / x._1 + x._2 + "B"
        }
      }
      r + "B"
    }
  }

}
