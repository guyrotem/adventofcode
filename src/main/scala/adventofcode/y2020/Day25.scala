package adventofcode.y2020

object Day25 extends App {

  def transform(subject: Int, times: Int): Long = {
    var cur = 1L
    var loop = times
    while (loop > 0) {
      cur = (cur * subject) % 20201227L
      loop = loop - 1
    }
    cur
  }

  def loop(subject: Int, target: Int): Int = {
    var cur = 1L
    var index = 0
    while (cur != target) {
      cur = (cur * subject) % 20201227
      index = index + 1
    }
    index
  }

  //  demo
  println(
    transform(
      17807724,
      loop(7, 5764801)
    )
  )

  //  ⭐️
  println(
    transform(
      10705932,
      loop(7, 12301431)
    )
  )

}
