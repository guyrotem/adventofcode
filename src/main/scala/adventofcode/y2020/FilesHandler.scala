package adventofcode.y2020

import scala.io.Source

object FilesHandler {
  def read2020(day: Int) = {
    val x = Source.fromFile(s"./src/main/inputs/2020/$day.txt")
    val input = x.getLines.toList
    x.close()
    input
  }
}
