package adventofcode.y2019

import scala.collection.mutable

object Day24 extends App {

  val Bug = "#"
  val EmptySpace = "."
  val BoardSize = 5 //  ODD

  type Board = Array[Array[String]]

  def parseInput(input: String) = {
    input.split('\n').filter(_.nonEmpty).map(_.split(""))
  }

  implicit class SuperBoard(board: Array[Array[String]]) {
    def getOption(row: Int, col: Int): Option[String] = {
      board.lift(row).flatMap(_.lift(col))
    }

    def countBugsAround(row: Int, col: Int): Int = {
      Seq(
        getOption(row + 1, col),
        getOption(row, col + 1),
        getOption(row - 1, col),
        getOption(row, col - 1),
      ).count(_.contains(Bug))
    }

    //  easier to view/print/compare
    def asSeq: Seq[Seq[String]] = board.toSeq.map(_.toSeq)

    def print(): Unit = asSeq.foreach(println)

    def compare(other: Array[Array[String]]): Boolean = asSeq == other.asSeq

    def biodiversity: Int = {
      board.zipWithIndex.map {
        case (row, rowIdx) =>
          row.zipWithIndex.map {
            case (cell, colIndex) =>
              if (cell == Bug) {
                0x1 << (BoardSize * rowIdx + colIndex)
              } else 0
          }.sum
      }.sum
    }

    def countBugs: Int = board.flatten.count(_ == Bug)
  }

  def next(board: Array[Array[String]]) = {
    board.zipWithIndex.map {
      case (row, rowIndex) =>
        row.zipWithIndex.map {
          case (cell, colIndex) =>
            val bugsAround = board.countBugsAround(rowIndex, colIndex)
            if (cell == Bug) {
              if (bugsAround == 1) Bug else EmptySpace
            } else {
              if (bugsAround == 1 || bugsAround == 2) Bug else EmptySpace
            }
        }
    }
  }

  assert(
    next(parseInput(Day24Input.demo)) compare parseInput(Day24Input.demo2ndPhase)
  )

  lazy val boardStream: LazyList[Board] = parseInput(Day24Input.input) #:: boardStream.map(next)

  //  because Array(s) are compared by ref
  val boardsSeen = mutable.Set[Seq[Seq[String]]]()

  val doubleBoard = boardStream.find { board =>
    val boardSeq = board.asSeq
    val result = boardsSeen.contains(boardSeq)
    boardsSeen.add(boardSeq)
    result
  }

  doubleBoard.foreach { b =>
    println(b.biodiversity) //  32506911
    b.print()
  }

  //  part II
  val EmptyBoard = Array.fill(BoardSize)(Array.fill(BoardSize)(EmptySpace))
  type MultiBoard = Seq[Board]
  case class Location(level: Int, row: Int, col: Int)
  val MiddleIndex = (BoardSize - 1) / 2

  def currentLevelNeighbors(row: Int, col: Int) = {
    Seq(
      Location(0, row + 1, col),
      Location(0, row, col + 1),
      Location(0, row - 1, col),
      Location(0, row, col - 1),
    )
      .filter(cell => cell.row >= 0 && cell.row < BoardSize && cell.col >= 0 && cell.col < BoardSize)
      .filterNot(cell => cell.col == MiddleIndex && cell.row == MiddleIndex)
  }

  def outerNeighbours(row: Int, col: Int) = {
    val rowNeighbors = Map(
      0 -> Location(1, MiddleIndex - 1, MiddleIndex),
      (BoardSize - 1) -> Location(1, MiddleIndex + 1, MiddleIndex),
    )

    val colNeighbors = Map(
      0 -> Location(1, MiddleIndex, MiddleIndex - 1),
      (BoardSize - 1) -> Location(1, MiddleIndex, MiddleIndex + 1),
    )

    rowNeighbors.get(row).toList ++ colNeighbors.get(col).toList
  }

  def innerNeighbours(row: Int, col: Int) = {
    if (row == MiddleIndex - 1 && col == MiddleIndex)
      (0 until BoardSize).map(Location(-1, 0, _))
    else if (row == MiddleIndex + 1 && col == MiddleIndex)
      (0 until BoardSize).map(Location(-1, BoardSize - 1, _))
    else if (row == MiddleIndex && col == MiddleIndex - 1)
      (0 until BoardSize).map(Location(-1, _, 0))
    else if (row == MiddleIndex && col == MiddleIndex + 1)
      (0 until BoardSize).map(Location(-1, _, BoardSize - 1))
    else Nil
  }

  def neighbours(row: Int, col: Int): Seq[Location] = {
    currentLevelNeighbors(row, col) ++
    outerNeighbours(row, col) ++
    innerNeighbours(row, col)
  }

  def nextRecursiveSpace(space: MultiBoard) = {
    (-1 until space.length+1).map { index =>

      val outer = space.lift(index - 1).getOrElse(EmptyBoard)
      val middle = space.lift(index).getOrElse(EmptyBoard)
      val inner = space.lift(index + 1).getOrElse(EmptyBoard)

      def readLocation(location: Location) = {
        val layer =
          if (location.level == 1) outer
          else if (location.level == -1) inner
          else if (location.level == 0) middle
          else throw new RuntimeException(s"what level?? ${location.level}")

        layer(location.row)(location.col)
      }

      (0 until BoardSize).toArray.map { row =>
        (0 until BoardSize).toArray.map { col =>
          val cellNeighbors = neighbours(row, col)
          val bugsAround = cellNeighbors.count(l => readLocation(l) == Bug)

          if (row == MiddleIndex && col == MiddleIndex) {
            EmptySpace
          } else if (readLocation(Location(0, row, col)) == Bug) {
            if (bugsAround == 1) Bug else EmptySpace
          } else {
            if (bugsAround == 1 || bugsAround == 2) Bug else EmptySpace
          }
        }
      }
    }
  }

  def evolveRecursiveSpace(initialBoard: Board, numberOfSeconds: Int): MultiBoard = {
    lazy val evolution: LazyList[MultiBoard] = Seq(initialBoard) #:: evolution.map(nextRecursiveSpace)
    evolution(numberOfSeconds)
  }

  val result = evolveRecursiveSpace(parseInput(Day24Input.input), 200)

  println(result.map(_.countBugs).sum)  //  2025

}

object Day24Input {
  val demo =
    """
      |....#
      |#..#.
      |#..##
      |..#..
      |#....
      |""".stripMargin

  val demo2ndPhase =
    """
      |#..#.
      |####.
      |###.#
      |##.##
      |.##..
      |""".stripMargin

  val input =
    """
      |##.#.
      |#..#.
      |.....
      |....#
      |#.###
      |""".stripMargin
}
