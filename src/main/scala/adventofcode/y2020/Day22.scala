package adventofcode.y2020

import scala.collection.mutable

object Day22 extends App {

  val input =
    """Player 1:
      |29
      |30
      |44
      |35
      |27
      |2
      |4
      |38
      |45
      |33
      |50
      |21
      |17
      |11
      |25
      |40
      |5
      |43
      |41
      |24
      |12
      |19
      |23
      |8
      |42
      |
      |Player 2:
      |32
      |13
      |22
      |7
      |31
      |16
      |37
      |6
      |10
      |20
      |47
      |46
      |34
      |39
      |1
      |26
      |49
      |9
      |48
      |36
      |14
      |15
      |3
      |18
      |28""".stripMargin

  val demoInput =
    """Player 1:
      |9
      |2
      |6
      |3
      |1
      |
      |Player 2:
      |5
      |8
      |4
      |7
      |10""".stripMargin

  def parseInput(input: String) = {
    val parsedInput = input.split("\n\n").map(_.split("\n").drop(1).map(_.toInt).toList)
    mutable.Queue.from(parsedInput(0)) -> mutable.Queue.from(parsedInput(1))
  }

  def encodeDeck(
                  list1: mutable.Queue[Int],
                  list2: mutable.Queue[Int],
                ) = {
//    list.map(x => 1L << x).sum
    if (list1.length < list2.length)
      list1.clone()
    else
      list2.clone()
  }

  var maxDepth = 0

  var gameCounter = 0

  def play(player1: mutable.Queue[Int], player2: mutable.Queue[Int]): (Boolean, mutable.Queue[Int]) = {
    def playInner(player1: mutable.Queue[Int], player2: mutable.Queue[Int], depth: Int): Boolean = {
      gameCounter = gameCounter + 1
      val knownConfigs = mutable.Set[mutable.Queue[Int]]()

      while (player1.nonEmpty && player2.nonEmpty) {
        val encoded = encodeDeck(player1, player2)

        if (knownConfigs.contains(encoded)) {
          return true
        }

        knownConfigs.add(encoded)

        val x1 = player1.dequeue()
        val x2 = player2.dequeue()
        if (x1 <= player1.length && x2 <= player2.length) {
          if (playInner(player1.clone().take(x1), player2.clone().take(x2), depth+1)) {
            player1.enqueue(x1, x2)
          } else {
            player2.enqueue(x2, x1)
          }
        } else if (x1 > x2) {
          player1.enqueue(x1, x2)
        } else {
          player2.enqueue(x2, x1)
        }
      }

      player1.nonEmpty
    }

    val res = playInner(player1, player2, 0)

    res -> (if (res) player1 else player2)
  }

  //  part 1
  val (player1A, player2A) = parseInput(input)

  while (player1A.nonEmpty && player2A.nonEmpty) {
    val x1 = player1A.dequeue()
    val x2 = player2A.dequeue()
    if (x1 > x2) {
      player1A.enqueue(x1, x2)
    } else {
      player2A.enqueue(x2, x1)
    }
  }

  println(calcScore(player1A.toList))
  println(calcScore(player2A.toList))

  def calcScore(l: List[Int]): Int = {
    (l :+ 0).reverse.zipWithIndex.map(x => x._1 * x._2).sum
  }

  assert(calcScore(play(parseInput(demoInput)._1, parseInput(demoInput)._2)._2.toList) == 291, "OOPS")
  val (player1B, player2B) = parseInput(input)
  val part2 = play(player1B, player2B)._2

  println(calcScore(part2.toList))

}
