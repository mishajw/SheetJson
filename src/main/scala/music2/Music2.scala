package music2

import java.lang.Math.sin

import music2.util.Output

object Music2 {
  def main(args: Array[String]) {
    for (i <- 0 to 1000) {
      Output play sin(i)
    }
  }
}
