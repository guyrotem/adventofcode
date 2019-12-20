package adventofcode.y2019

import scala.collection.mutable

object Day18 extends App {

  case class Item(value: String, location: Location) {
    var _visited: Boolean = _
    var _distance: Int = _

    def visit(distance: Int): Unit = {
      _visited = true
      _distance = distance
    }

    def reset(): Unit = {
      _visited = false
      _distance = -1
    }

    reset()
  }

  case class Location(x: Int, y: Int)

  class Maze(
              initialStartPoint: Location,
              _items: Seq[Seq[Item]],
            ) {
    var startPoint: Location = initialStartPoint
    var items: Seq[Seq[Item]] = _items

    val keysTotal: Int = _items.flatten.count(c => isKey(c.value))
    private val height = items.length
    private val width = items(0).length

    def visit(location: Location, distance: Int): Item = {
      val item = items(location.y)(location.x)
      item.visit(distance)
      item
    }

    def visited(location: Location): Boolean = {
      items(location.y)(location.x)._visited
    }

    def neighbors(cell: Location): Seq[Location] = {
      Seq(
        if (cell.x < width - 1) Some(Location(cell.x + 1, cell.y)) else None,
        if (cell.y < height - 1) Some(Location(cell.x, cell.y + 1)) else None,
        if (cell.x > 0) Some(Location(cell.x - 1, cell.y)) else None,
        if (cell.y > 0) Some(Location(cell.x, cell.y - 1)) else None,
      ).flatten
    }

    def draw(): Unit = {
      items.foreach(row => println(row.map(format).mkString("")))
    }

    def format(item: Item): String = {
      if (item._visited && item.value == ".") " "
      else item.value
    }

    def reset(_startPoint: Location): Unit = {
      items.foreach(_.foreach(_.reset()))
      startPoint = _startPoint
    }
  }

  class MultiMaze(
                   initialStartPoint: Seq[Location],
                   _items: Seq[Seq[Item]],
                 ) {
    var startPoints: Seq[Location] = initialStartPoint
    var items: Seq[Seq[Item]] = _items

    val keysTotal: Int = _items.flatten.count(c => isKey(c.value))
    private val height = items.length
    private val width = items(0).length

    def visit(location: Location, distance: Int): Item = {
      val item = items(location.y)(location.x)
      item.visit(distance)
      item
    }

    def visited(location: Location): Boolean = {
      items(location.y)(location.x)._visited
    }

    def neighbors(cell: Location): Seq[Location] = {
      Seq(
        if (cell.x < width - 1) Some(Location(cell.x + 1, cell.y)) else None,
        if (cell.y < height - 1) Some(Location(cell.x, cell.y + 1)) else None,
        if (cell.x > 0) Some(Location(cell.x - 1, cell.y)) else None,
        if (cell.y > 0) Some(Location(cell.x, cell.y - 1)) else None,
      ).flatten
    }

    def draw(): Unit = {
      items.foreach(row => println(row.map(format).mkString("")))
    }

    def format(item: Item): String = {
      if (item._visited && item.value == ".") " "
      else item.value
    }

    def reset(_startPoints: Seq[Location]): Unit = {
      items.foreach(_.foreach(_.reset()))
      startPoints = _startPoints
    }
  }

  def parseInput(input: String): Maze = {
    val textMatrix = input.split('\n').map(_.trim.split("").filter(_.nonEmpty)).filter(_.nonEmpty)

    val maze = textMatrix.toSeq.zipWithIndex.map {
      case (row, rowIdx) => row.toSeq.zipWithIndex.map {
        case (cell, colIdx) =>
          Item(cell, Location(colIdx, rowIdx))
      }
    }

    new Maze(
      maze.flatMap(_.find(item => item.value == "@")).map(_.location).head,
      maze.map(_.filter(_.value.trim.nonEmpty).map(x => if (x.value == "@") x.copy(value = ".") else x)).filter(_.nonEmpty)
    )
  }

  def isKey(value: String) = {
    value(0) >= 'a' && value(0) <= 'z'
  }

  def isDoor(value: String) = {
    value(0) >= 'A' && value(0) <= 'Z'
  }

  val KeyValues = (97 to 122).map(_.toChar.toString)

  def bfs(maze: Maze, startFrom: Location, availableKeys: Seq[String]): Unit = {
    maze.reset(startFrom)
    val queue = mutable.Queue.empty[(Location, Int)]

    val canWalkThrough = mutable.Set[String](".")
    canWalkThrough.addAll(KeyValues)
    canWalkThrough.addAll(availableKeys.map(_.toUpperCase()))

    queue.enqueue(maze.startPoint -> 0)

    while (queue.nonEmpty) {
      val (firstInLine, distance) = queue.dequeue()
      val nextValue = maze.visit(firstInLine, distance).value

      if (canWalkThrough.contains(nextValue)) {
        if (!isKey(nextValue) || availableKeys.contains(nextValue)) {
          maze.neighbors(firstInLine)
            .filterNot(maze.visited)
            .foreach(item => queue.enqueue(item -> (distance + 1)))
        }

      }

    }
  }

  implicit val multiMazePathOrdering = new Ordering[MultiMazePath] {
    override def compare(x: MultiMazePath, y: MultiMazePath): Int = {

      if (x == y) 0
      else {
        var temp = x.length compareTo y.length
        if (temp != 0) temp
        else {
          temp = x.end.toString compareTo y.end.toString
          if (temp != 0) temp
          else {
            x.keys.hashCode() compareTo y.keys.hashCode()
          }
        }
      }
    }
  }

  implicit val mazePathOrdering = new Ordering[MazePath] {
    override def compare(x: MazePath, y: MazePath): Int = {
      if (x == y) 0
      else {
        var temp = x.length compareTo y.length
        if (temp != 0) temp
        else {
          temp = x.end.x compareTo y.end.x
          if (temp != 0) temp
          else {
            temp = x.end.y compareTo y.end.y
            if (temp != 0) temp
            else {
              x.keys.hashCode() compareTo y.keys.hashCode()
            }
          }
        }
      }
    }
  }

  case class MazePath(keys: Set[String], end: Location, length: Int)
  case class MultiMazePath(keys: Set[String], end: Seq[Location], length: Int)

  def solveA(input: String): Int = {
    val maze = parseInput(input)
    println(maze.startPoint)
    println(s"number of keys: ${maze.keysTotal}")
    maze.draw()
    var shortestPath: Int = Int.MaxValue

    val paths = mutable.SortedSet[MazePath](MazePath(Set.empty, maze.startPoint, 0))

    while (paths.nonEmpty) {
      if (Math.random() < 0.0001) println(s"ITERATING ${paths.min} ${paths.size}")

      val path = paths.min
      paths.remove(path)

      if (path.length > shortestPath)
        return shortestPath

      bfs(maze, path.end, path.keys.toSeq)
      collectVisitedKeys(maze).filterNot(item => path.keys.contains(item.value)).foreach { item =>
        val newKeys = path.keys + item.value
        val newLength = item._distance + path.length
        val newPath = MazePath(newKeys, item.location, newLength)

        paths.add(newPath)

        if (newKeys.size == maze.keysTotal && newLength < shortestPath) {
          shortestPath = newLength
          println("found something shorter: " + shortestPath)
        }
      }
    }
    shortestPath
  }

  def parseInputB(input: String): MultiMaze = {
    val textMatrix = input.split('\n').map(_.trim.split("").filter(_.nonEmpty)).filter(_.nonEmpty)

    val rowIndex = textMatrix.indexWhere(_.indexOf("@") >= 0)
    val colIndex = textMatrix(rowIndex).indexOf("@")

    val inputSplit = textMatrix.take(rowIndex - 1) :+
      textMatrix(rowIndex - 1).zipWithIndex.collect({
        case (_, idx) if idx == colIndex - 1 => "@"
        case (_, idx) if idx == colIndex => "#"
        case (_, idx) if idx == colIndex + 1 => "@"
        case (c, _) => c
      }) :+
      textMatrix(rowIndex).zipWithIndex.collect({
        case (_, idx) if colIndex - 1 <= idx && idx <= colIndex + 1 => "#"
        case (c, _) => c
      }) :+
      textMatrix(rowIndex + 1).zipWithIndex.collect({
        case (_, idx) if idx == colIndex - 1 => "@"
        case (_, idx) if idx == colIndex => "#"
        case (_, idx) if idx == colIndex + 1 => "@"
        case (c, _) => c
      }) :++
      textMatrix.takeRight(textMatrix.length - rowIndex - 2)


    val maze = inputSplit.toSeq.zipWithIndex.map {
      case (row, rowIdx) => row.toSeq.zipWithIndex.map {
        case (cell, colIdx) =>
          Item(cell, Location(colIdx, rowIdx))
      }
    }

    new MultiMaze(
      maze.flatMap(_.filter(item => item.value == "@")).map(_.location),
      maze.map(_.filter(_.value.trim.nonEmpty).map(x => if (x.value == "@") x.copy(value = ".") else x)).filter(_.nonEmpty)
    )
  }

  implicit class MultiMazeShrink(mm: MultiMaze) {
    def asMaze(start: Location) = {
      new Maze(start, mm.items)
    }
  }

  def solveB(input: String): Int = {
    val maze = parseInputB(input)
    println(maze.startPoints)
    println(s"number of keys: ${maze.keysTotal}")
    maze.draw()
    var shortestPath: Int = Int.MaxValue

    val paths = mutable.SortedSet(MultiMazePath(Set.empty, maze.startPoints, 0))

    while (paths.nonEmpty) {
      if (Math.random() < 0.0002) println(s"${paths.min} ${paths.size}")

      val path = paths.min
      paths.remove(path)

      if (path.length > shortestPath)
        return shortestPath

      path.end.map(start => {
        bfs(maze.asMaze(start), start, path.keys.toSeq)
        collectVisitedKeys(maze).filterNot(item => path.keys.contains(item.value)).foreach { item =>
          val newKeys = path.keys + item.value
          val newLength = item._distance + path.length

          val newPath = MultiMazePath(newKeys, path.end.filterNot(_ == start) :+ item.location, newLength)

          paths.add(newPath)

          if (newKeys.size == maze.keysTotal && newLength < shortestPath) {
            shortestPath = newLength
            println("found something shorter: " + shortestPath)
          }
        }
      })
    }
    shortestPath
  }

  def collectVisitedKeys(maze: Maze) = {
    maze.items.flatten.filter(cell => isKey(cell.value) && cell._distance > 0)
  }

  def collectVisitedKeys(maze: MultiMaze) = {
    maze.items.flatten.filter(cell => isKey(cell.value) && cell._distance > 0)
  }

  assert(solveA(Day18Input.demo86) == 86)
  assert(solveA(Day18Input.demo132) == 132)
  assert(solveA(Day18Input.demo136) == 136)
  assert(solveA(Day18Input.demo81) == 81)

  println(solveA(Day18Input.input))  //  3586 (slow)
  println(solveB(Day18Input.input)) //  1974 (slooow)

}

object Day18Input {
  val demo132 =
    """
      |########################
      |#...............b.C.D.f#
      |#.######################
      |#.....@.a.B.c.d.A.e.F.g#
      |########################
      |""".stripMargin

  val demo86 =
    """
      |########################
      |#f.D.E.e.C.b.A.@.a.B.c.#
      |######################.#
      |#d.....................#
      |########################
      |""".stripMargin

  val demo136 =
    """
      |#################
      |#i.G..c...e..H.p#
      |########.########
      |#j.A..b...f..D.o#
      |########@########
      |#k.E..a...g..B.n#
      |########.########
      |#l.F..d...h..C.m#
      |#################
      |""".stripMargin

  val demo81 =
    """
      |########################
      |#@..............ac.GI.b#
      |###d#e#f################
      |###A#B#C################
      |###g#h#i################
      |########################
      |""".stripMargin

  val input =
      """
        |#################################################################################
        |#..f....#...........#.....#.........#m..#.........#.#.......#...............#...#
        |#.#.###.#########.#.#.###.#.#.#####.#.#.#Q#.#####.#.#.###.###.#V#####.#####.#.#C#
        |#.#...#...........#t#.#.#...#.#...#.#.#.#.#.#.......#.#.#.....#.....#.#...#...#.#
        |#####.###############.#.#####.#.#.###.#.#.#.#########.#.###########.###.#.#####.#
        |#.....#...............#...#.#...#...#.#.#.#.........#.#...........#.....#.#...#e#
        |#.###.#.###############.#.#.#######.#I#.#.#########.#.###########.#####.#.#.#.#.#
        |#...#.#...#.....#.......#.....#.G.#.#.#.#.#.......#.#.....#.......#...#.#...#.#.#
        |###.#.###.#.#.#.###.###########.#.#.#.#.#.#####.#.#.#####.#.#.#####.#.#######.#.#
        |#...#.#...#.#.#...#.#...#.....#b#.#...#.#.......#.#.........#.#.....#.#...#...#.#
        |#.#####.###.#.###.#.#.#.#.###.#.#.#####.#########.#############.#####.#.#.#.###.#
        |#.......#.#.#...#.Z.#.#.B.#.....#.....#.#.......#.....#.........#.#.....#...#...#
        |#.#######.#.###.#####.#.#############.#M#.#.#########.#.#########.#.#############
        |#.#.......#...#.#.....#u#...K.......#.#.#.#...........#...#.........#...........#
        |#.###.###.#.###.#.#####.#.#####.###.#.#.#.#############.#.#.#########.#########.#
        |#...#...#...#...#.U.#...#.#..j#...#.#g..#.......#...R.#.#.#.....#.....#.......#.#
        |###.#######.#.#####.#####.#.#.###.#.#############.###.#.#.#######.#########.###.#
        |#.#.......#.#.....#..k....#.#.#d..#.#...#.........#...#.#.........#.#.......#...#
        |#.#######.#.#####.#####.###.#.#####.#.#.#.#########.###.#########.#.#.#.#####.#.#
        |#.....#...#.#.....#.H.#.#...#.S...#.D.#.#...#.....#.#...#.#.....#.#...#..a#...#.#
        |#.#.#.#.#####.#####.#.#.#.#######.#####.###.#.###.#.###.#.#.###.#.#.#####.#.###.#
        |#.#.#.#...#...#...#.#.#.#.#.....#.....#s#...#...#.#...#...#...#.#.#...#...#.#...#
        |###.#.###.#.###.#.#.#####J###.#######.#.#.#######.###.###.#####.#.#.###.#.#.#.###
        |#...#.#...#.#...#.#.#.....#...#.....#...#.#...#.....#.#...#...#.#.#.#...#.#.#...#
        |#.#####.###.#.###.#.###.###.###.#.#.#####.#.#.#.###.#.###.###.#.#.###.###.#.###.#
        |#.....#...#...#...#....h#...#...#.#.#...#...#.#.#.#.#...#.....#.#.....#...#.#.#.#
        |#.###.###.#.###.###########.#.###.#.###.#####.#.#.#.###.#######.#######.###.#.#.#
        |#.#.#.....#.#...#.....#...#...#.#.#.....#...#...#...#.#...#.....#.......#.#.#..p#
        |#.#.#########.###.#.#.#.#.###.#.#.#####.#.#######.###.###.#.###########.#.#.###.#
        |#.#.#.......#.....#.#...#...#...#.#...#.#.......#.#.#...#.#.......#...#.#.#...#.#
        |#.#.#.###.#.#.#####.#######.#####.###.#.#.#####.#.#.#.#.#.#######.#.#.#.#.###.#.#
        |#.#...#...#.#.....#.......#.....#...#...#.....#.#.#...#.#...#...#...#.#.#.#...#.#
        |#.#####.###.#####.#######.#####.###.#.###.#####.#.#####.#.###.#.#####.#.#.#.###.#
        |#...N.#.#.#.#.....#.......#...#.....#.#.#.#...#.#.......#.....#.......#...#...#.#
        |#####.#.#.#.#.#####.#######.#########.#.###.#.#.#######.#####################.#.#
        |#...#...#...#.....#.......#.#...#.....#.#...#.....#...X.#.....Y.#...#...W.....#.#
        |###.#####.#######.#######.#.#.#.#.#####.#.#########.#########.#.###.#.#.#######.#
        |#...#.....#...#.....#...#.#.#.#...#.....#.#.....#...#...#...#.#...#...#.#.#.....#
        |#.###.#####.#.#######.#.#.#.#.#######.#.#.#.###.#.###.#.#.#.#.###.#####.#.#.#####
        |#.........A.#.........#...#..x........#.......#.......#...#...#.........#.......#
        |#######################################.@.#######################################
        |#.O.......#.............#.......#.....................#...........#...#...#.#...#
        |###.#####.#######.#####.###.#.#.#.#####.#.#.#########.#######.###.#.#.#.#.#.#.#.#
        |#...#...#.......#.#..y....#.#.#.#.#...#.#.#.#.....#.........#...#.#.#...#.#.#.#.#
        |#.#####.#######.#.#.#####.###.#.#.#.#.#.###.#.###.#########.###.#.#.#####.#.#.#.#
        |#.#...#.......#.#.#.#...#.....#...#.#.#.#...#...#...#.....#.....#.#...#.#.#...#.#
        |#.#.#.#####.###.#.###.#.#############.#.#.#####.###.#.###.#######.###.#.#.#####.#
        |#..r#.#...#.....#.#.L.#.#...#...#.....#.#.........#.#.#...#.....#.....#.#.#..n..#
        |#####.#.#.###.###.#.###.#.#.#.#.###.###.#.#######.#.#.#.###.###.#######.#.#.#####
        |#...#...#...#..w..#.#.#.#.#.#.#...#...#.#...#...#.#...#...#.#...#.....#...#.....#
        |#.#.#######.#######.#.#.#.#.#.###.###.#.###.#.#.#######.#.#.#.#####.###.###.###.#
        |#.#...#...#.....#...#.#...#.....#...#.#.#.#.#.#.......#.#...#.#...#.#...#...#...#
        |###.#.#.#######.#.###.#########.###.#.#.#.#.#.#######.#.#####.#.#.#.#.#####.#.###
        |#...#...#...#.P.#.#...#.#.....#.#...#.#.#...#.#.#...#.#...#o#...#.#...#...#.#...#
        |#.#####.#.#.#.###.#.#.#.#.###.###.###.#.#.###.#.#.#.#.###.#.#####.#.###.#.#.###.#
        |#.....#.#.#.#.....#.#...#...#.....#...#.#...#.#...#.#.#.....#.....#.#...#.#...#.#
        |#.###.#.#.#.#######.#######.#########.#.#####.#.###.#.#.#####.#####.#.###.#.###.#
        |#.#.#.#.#.#...#.#.....#.....#...#...#.#.#.....#.#.#.#.#.#.....#...#.#...#.#.#...#
        |#.#.#.###.#.#.#.#.###.#.#####.#.#.#.#.#.#.#####.#.#.#.###.#####.###.###.#.###.###
        |#.#.#.E...#.#.#...#...#...#...#...#...#.#.....#.#.#.......#.........#..l#...#...#
        |#.#.#######.#.#####.###.#.#.#.#####.###.#####.#.#.#########.###.#####.#####.###.#
        |#.#.......#.#.....#.#.#.#.#.#.#...#.#...#.....#.#.#.....#...#.#.#...#.#...#.....#
        |#.#.#####.#.#####.#.#.#.#.###.#.#.###.###.#####.#.#.###.#####.#.#.#.#.###.#####.#
        |#.#.#.....#.#.......#.#.#.#...#.#.#...#.#.#.....#.#.#.#.....#.#...#...#...#.....#
        |#.#.#.#####.#.#######.#.#.#.###.#.#.###.#.#.#####.#.#.#####.#.#########.###.#####
        |#.#.#...#...#.#..q#...#.#.#.....#.#.#...#.#.#.....#.#.....#.#.......#...#...#...#
        |#.#.#.###.###.#.#.#.#####.#.#####.#.###.#.#.#.#.###.###.#.#.#######.#.#.#.#####.#
        |#.#.#.#...#.#...#.#.......#.....#.#...#.#.#...#.#v..#...#.#...#.....#.#.#.#...#.#
        |#.#.#.#.###.#.#################.#.###.#.#.###.###.###.#######.#.#####.###.#.#.#.#
        |#...#.#...#...#.............#...#...#.#.#...#.#...#.........#...#.......#...#...#
        |#.#####.#.#####.#.###########.#####.#.#.#.#.###.###########.#####.#####.#######.#
        |#.#...#.#.#.....#.....#.......#.....#.#.#.#...#.#...............#.#...#.......#.#
        |###.#.###.#.#########.#.#######.#####.#.#.###.#.#.#.#############.###.#####.###.#
        |#...#...#.#.#.........#.#.....#.....#.#.#.#...#.#.#....c..........#.....#.#.....#
        |#.#####.#.#.#.#########.#.#.#.#####.#.#.###.###.#.#################.###.#.#######
        |#.....#...#z#.......#...#.#.#...#.#.#.#.#...#...#.....#.....#...#...#.#.#.......#
        |#.###.#####.#######.#.###.#.###.#.#.#.#.#.###.#######.###.#.#.#.#.###.#.#T#.###.#
        |#...#.....#...#...#...#...#...#...#.#.#.#...#.#...#...#...#.#.#...#.#...#.#.#.#.#
        |###.#####.###.#.#.###########.###.#.#.#.#.#.#.#.###.###.###.#.#####.#F#####.#.#.#
        |#.......#.......#.............#...#.....#.#...#.........#.....#.............#i..#
        |#################################################################################
        |""".stripMargin

}
