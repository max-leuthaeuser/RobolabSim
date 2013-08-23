package tud.robolab.utils

import java.util.regex.Pattern

object IPUtils {
  val IPV4_REGEX = """(([0-1]?[0-9]{1,2}\.)|(2[0-4][0-9]\.)|(25[0-5]\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))"""
  val IPV4_PATTERN = Pattern.compile(IPV4_REGEX)

  def isValidIPV4(s: String) = IPV4_PATTERN.matcher(s).matches()
}
