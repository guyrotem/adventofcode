package adventofcode.y2019

object Day10 extends App {

  //  parsers
  def parseInput(input: String) = {
    input.split("\n")
      .map(_.split("")
        .map(_ == "#")
      )
  }

  def visualize(matrix: Array[Array[Boolean]]): Unit = {
    matrix.foreach(row => println(row.map(x => if (x) "+" else " ").toSeq))
  }

  def asteroidIndices(matrix: Array[Array[Boolean]]) = {
    matrix.zipWithIndex
      .map {
        case (row, rowIndex) =>
          row.zipWithIndex.collect {
            case (asteroid, columnIndex) if asteroid =>
              columnIndex -> rowIndex
          }
      }.flatten
      .map({case (x, y) => Coordinate(x, y)})
  }

  //  inputs
  val input1 = parseInput(
    """|.#..#
       |.....
       |#####
       |....#
       |...##
       |""".stripMargin)
  val asteroids1 = asteroidIndices(input1)

  val input2 = parseInput(
    """.#..##.###...#######
      |##.############..##.
      |.#.######.########.#
      |.###.#######.####.#.
      |#####.##.#.##.###.##
      |..#####..#.#########
      |####################
      |#.####....###.#.#.##
      |##.#################
      |#####.##.###..####..
      |..######..##.#######
      |####.##.####...##..#
      |.#####..#.######.###
      |##...#.##########...
      |#.##########.#######
      |.####.#.###.###.#.##
      |....##.##.###..#####
      |.#.#.###########.###
      |#.#.#.#####.####.###
      |###.##.####.##.#..##""".stripMargin
  )
  val asteroids2 = asteroidIndices(input2)

  val input3 = parseInput("""...###.#########.####
                 |.######.###.###.##...
                 |####.########.#####.#
                 |########.####.##.###.
                 |####..#.####.#.#.##..
                 |#.################.##
                 |..######.##.##.#####.
                 |#.####.#####.###.#.##
                 |#####.#########.#####
                 |#####.##..##..#.#####
                 |##.######....########
                 |.#######.#.#########.
                 |.#.##.#.#.#.##.###.##
                 |######...####.#.#.###
                 |###############.#.###
                 |#.#####.##..###.##.#.
                 |##..##..###.#.#######
                 |#..#..########.#.##..
                 |#.#.######.##.##...##
                 |.#.##.#####.#..#####.
                 |#.#.##########..#.##.""".stripMargin)
  val asteroids3 = asteroidIndices(input3)

  //  graph utils
  def shrink(n: Int, d: Int): (Int, Int) = {
    val divBy = gcd(Math.abs(n), Math.abs(d))

    (n / divBy) -> (d / divBy)
  }

  @scala.annotation.tailrec
  def gcd(num1: Int, num2: Int): Int = {
    val max = Math.max(num1, num2)
    val min = Math.min(num1, num2)

    if (min == 0) max else gcd(max % min, min)
  }

  case class Coordinate(x: Int, y: Int)
  case class Direction(x: Int, y: Int)
  case class AsteroidVector(direction: Direction, length: Int)

  def calcDirectionVector(from: Coordinate, to: Coordinate): Direction = {
    val dir = shrink(to.x - from.x, to.y - from.y)
    Direction(dir._1, dir._2)
  }

  def calcVisibleAsteroids(asteroids: Seq[Coordinate], fromLocation: Coordinate) = {
    asteroids.filterNot(_ == fromLocation).map(calcDirectionVector(fromLocation, _)).distinct.length
  }

  def findMaxVisibleAsteroids(asteroids: Seq[Coordinate]) = {
    val viewsPerAsteroid = asteroids.map { location =>
      location -> calcVisibleAsteroids(asteroids, location)
    }
    viewsPerAsteroid.maxBy(_._2)._1
  }

  visualize(input1)
  println(findMaxVisibleAsteroids(asteroids1))
  visualize(input2)
  println(findMaxVisibleAsteroids(asteroids2))
  visualize(input3)
  println(findMaxVisibleAsteroids(asteroids3))

  //  part 2
  def distance(from: Coordinate, to: Coordinate): Int = {
    (to.y - from.y) * (to.y - from.y) + (to.x - from.x) * (to.x - from.x)
  }

  def calcVectorAndDistance(from: Coordinate, to: Coordinate): AsteroidVector = {
    AsteroidVector(calcDirectionVector(from, to), distance(from, to))
  }

  def angle(direction: Direction) = {
    import direction._
    val normal =
      if (y == 0 && x > 0) 90.0
      else if (y == 0 && x < 0) 270.0
      else if (x >= 0 && y > 0) Math.atan(x.toDouble / y) * 180 / Math.PI
      else if (y < 0) 180 + Math.atan(x.toDouble / y) * 180 / Math.PI
      else 360 + Math.atan(x.toDouble / y) * 180 / Math.PI

    //  because down is up :( and result must be in range [0, 360)
    if (normal <= 180)
      180 - normal
    else
      540 - normal
  }

  def destroy360(asteroids: Seq[Coordinate], base: Coordinate) = {
    asteroids.filterNot(_ == base).map(asteroid =>
      asteroid -> calcVectorAndDistance(base, asteroid)
    )
      .groupBy(_._2.direction)
      .values.map(_.minBy(_._2.length)).toSeq.sortBy(x => angle(x._2.direction)).map(_._1)
  }

  case class RoundResult(destroyed: Seq[Coordinate], left: Seq[Coordinate])

  def nextRound(roundResult: RoundResult, base: Coordinate): RoundResult = {
    val nextDestroyed = destroy360(roundResult.left, base)
    val nextLeft = nextDestroyed.filterNot(roundResult.destroyed.contains)
    RoundResult(nextDestroyed, nextLeft)
  }

  def giantLazerByDestroyOrder(asteroids: Seq[Coordinate]) = {
    val base = findMaxVisibleAsteroids(asteroids)
    lazy val rounds: LazyList[Option[RoundResult]] = Some(RoundResult(Nil, asteroids)) #:: rounds.map(_.flatMap(round => {
      if (round.left.nonEmpty || round.destroyed.nonEmpty) Some(nextRound(round, base))
      else None
    }))

    rounds.takeWhile(_.isDefined).flatten.flatMap(_.destroyed)
  }

  val result2 = giantLazerByDestroyOrder(asteroids2)
  val result3 = giantLazerByDestroyOrder(asteroids3)

  println(Seq(1, 2, 3, 10, 20, 50, 100, 199, 200, 201, 299).map(x => result2(x - 1)))
  print(100 * result3(200 - 1).x + result3(200 - 1).y)

}
