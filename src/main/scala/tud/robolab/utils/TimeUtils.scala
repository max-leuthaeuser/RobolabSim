package tud.robolab.utils

object TimeUtils {
  /**
   * Use apply method to measure time used
   * executing the given function block.
   */
  object Time {
    def apply[T](name: String)(block: => T) {
      val start = System.currentTimeMillis
      try {
        block
      } finally {
        val diff = System.currentTimeMillis - start
        println("# " + name + " completed, time taken: " + diff + " ms (" + diff / 1000.0 + " s)")
      }
    }
  }
}