package adventofcode.y2020

import scala.collection.mutable

object Day23 extends App {

  val demo = "389125467"
  val input = "643719258"

  val cupsLength = 1000000

  class Cup(
             value: Int,
             var next: Cup,
           ) {
    def getValue: Int = value
  }

  val lookup = mutable.HashMap[Int, Cup]()

  def circleCups(input: List[Int]): List[Cup] = {
    val initial = input.map(new Cup(_, null)).toArray
    initial.zipWithIndex.foreach({
      case (cup, idx) =>
        cup.next = initial((idx + 1) % cupsLength)
        lookup.put(cup.getValue, cup)
    })
    initial.toList
  }

  def move(current: Cup): Cup = {
    val threeCups = current.next
    current.next = current.next.next.next.next
    val excluded = Set(threeCups.getValue, threeCups.next.getValue, threeCups.next.next.getValue)
    var destinationVal = if (current.getValue == 1) cupsLength else current.getValue - 1
    while (excluded.contains(destinationVal)) {
      destinationVal = if (destinationVal == 1) cupsLength else destinationVal - 1
    }

    val destinationCup: Cup = lookup.getOrElse(destinationVal, throw new RuntimeException(s"something is wrong $destinationVal"))
    threeCups.next.next.next = destinationCup.next
    destinationCup.next = threeCups
    current.next
  }

  val prefix = input.split("").map(_.toInt).toList

  val cupValues = prefix ++ ((prefix.length + 1) to cupsLength)
  val firstCup = circleCups(cupValues).head

  var currentCup = firstCup

  var round = 0
  while (round < 10000000) {
    currentCup = move(currentCup)
    round = round + 1
  }

  def getCupValues(cup: Cup, count: Int): List[Int] = {
    lazy val cupsStream: LazyList[Cup] = cup #:: cupsStream.map(_.next)
    cupsStream.take(count).toList.map(_.getValue)
  }

  while (currentCup.getValue != 1) {
    currentCup = currentCup.next
  }

  println(currentCup.next.getValue.toLong * currentCup.next.next.getValue)  //  146304752384
}
