package adventofcode.y2020

import java.io.File

import scala.collection.mutable

object Day24 extends App {
  val demo = """sesenwnenenewseeswwswswwnenewsewsw
               |neeenesenwnwwswnenewnwwsewnenwseswesw
               |seswneswswsenwwnwse
               |nwnwneseeswswnenewneswwnewseswneseene
               |swweswneswnenwsewnwneneseenw
               |eesenwseswswnenwswnwnwsewwnwsene
               |sewnenenenesenwsewnenwwwse
               |wenwwweseeeweswwwnwwe
               |wsweesenenewnwwnwsenewsenwwsesesenwne
               |neeswseenwwswnwswswnw
               |nenwswwsewswnenenewsenwsenwnesesenew
               |enewnwewneswsewnwswenweswnenwsenwsw
               |sweneswneswneneenwnewenewwneswswnese
               |swwesenesewenwneswnwwneseswwne
               |enesenwswwswneneswsenwnewswseenwsese
               |wnwnesenesenenwwnenwsewesewsesesew
               |nenewswnwewswnenesenwnesewesw
               |eneswnwswnwsenenwnwnwwseeswneewsenese
               |neswnwewnwnwseenwseesewsenwsweewe
               |wseweeenwnesenwwwswnew""".stripMargin

  println(new File(".").listFiles().toList)
  val input = FilesHandler.read2020(24)

  case class Coords(e: Int, ne: Int)

  def walk(current: Coords, dir: String): Coords = {
    dir match {
      case "e" =>
        Coords(current.e + 1, current.ne)
      case "ne" =>
        Coords(current.e, current.ne + 1)
      case "nw" =>
        Coords(current.e - 1, current.ne + 1)
      case "w" =>
        Coords(current.e - 1, current.ne)
      case "sw" =>
        Coords(current.e, current.ne - 1)
      case "se" =>
        Coords(current.e + 1, current.ne - 1)
    }
  }

  def parse(path: String): List[String] = {
    val steps = mutable.ArrayBuffer[String]()
    var i = 0
    while (i < path.length) {
      val ch = path(i)
      if (ch == 'e' || ch == 'w') {
        steps += ch.toString
        i = i + 1
      } else {
        steps += path.substring(i, i + 2)
        i = i + 2
      }
    }
    steps.toList
  }

  val blackTiles = input
    .map(parse)
    .map(_.foldLeft(Coords(0, 0))(walk))
    .groupBy(identity).filter(_._2.length % 2 == 1).keys.toList

  println(blackTiles.length)  //388

  //pt2

  def countNeighbors(searchBlack: Set[(Int, Int)], e: Int, ne: Int) = {
    List(
      (e + 1) -> ne,
      (e + 1) -> (ne - 1),
      e -> (ne + 1),
      e -> (ne - 1),
      (e - 1) -> (ne + 1),
      (e - 1) -> ne,
    ).count(searchBlack.contains)
  }

  def iterate(blackTiles: List[Coords]): List[Coords] = {
    val minE = blackTiles.minBy(_.e).e - 1
    val maxE = blackTiles.maxBy(_.e).e + 1
    val minNE = blackTiles.minBy(_.ne).ne - 1
    val maxNE = blackTiles.maxBy(_.ne).ne + 1

    val searchBlack = blackTiles.map(t => t.e -> t.ne).toSet
    val nextBlacks = mutable.ArrayBuffer[Coords]()

    (minE to maxE) foreach { e =>
      (minNE to maxNE) foreach { ne =>
        val blackNeighbors = countNeighbors(searchBlack, e, ne)
        if (searchBlack.contains(e -> ne)) {
          if (blackNeighbors == 1 || blackNeighbors == 2) {
            nextBlacks += Coords(e, ne)
          }
        } else {
          if (blackNeighbors == 2) {
            nextBlacks += Coords(e, ne)
          }
        }
      }
    }
    nextBlacks.toList
  }

  var phase = blackTiles
  debug(phase)
  (1 to 100).foreach { _ =>
    phase = iterate(phase)
  }
  println(phase.length)

  def debug(blacks: List[Coords]): Unit = {
    val rows = blacks.groupBy(_.ne)
    val globalEMin = blacks.minBy(_.e).e
    val globalEMax = blacks.maxBy(_.e).e
    val painture = (rows.minBy(_._1)._1 to rows.maxBy(_._1)._1).map { x =>
      val row = rows.getOrElse(x, Nil).sortBy(_.e).map(_.e)
      (globalEMin to globalEMax).map { y =>
        if (row.contains(y))
          "#"
        else
          "."
      }
    }
    println(painture.map(_.mkString("")).mkString("\n"))
  }
}
