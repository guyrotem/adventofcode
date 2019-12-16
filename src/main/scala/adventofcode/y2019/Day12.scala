package adventofcode.y2019

object Day12 extends App {

  //  DEMO 1
//    <x=-1, y=0, z=2>
//    <x=2, y=-10, z=-7>
//    <x=4, y=-8, z=8>
//    <x=3, y=5, z=-1>

  val demo1 =
    Seq(
      Seq(
        Moon1D(1, 0),
        Moon1D(2, 0),
        Moon1D(4, 0),
        Moon1D(3, 0),
      ),
      Seq(
        Moon1D(0, 0),
        Moon1D(-10, 0),
        Moon1D(-8, 0),
        Moon1D(5, 0),
      ),
      Seq(
        Moon1D(2, 0),
        Moon1D(-7, 0),
        Moon1D(8, 0),
        Moon1D(-1, 0),
      )
    )
  //  DEMO 2
//    <x=-8, y=-10, z=0>
//    <x=5, y=5, z=10>
//    <x=2, y=-7, z=3>
//    <x=9, y=-8, z=-3>

  val demo2 =
    Seq(
      Seq(
        Moon1D(-8, 0),
        Moon1D(5, 0),
        Moon1D(2, 0),
        Moon1D(9, 0),
      ),
      Seq(
        Moon1D(-10, 0),
        Moon1D(5, 0),
        Moon1D(-7, 0),
        Moon1D(-8, 0),
      ),
      Seq(
        Moon1D(0, 0),
        Moon1D(10, 0),
        Moon1D(3, 0),
        Moon1D(-3, 0),
      )
    )

  case class Location(x: Int, y: Int, z: Int)
  case class Velocity(x: Int, y: Int, z: Int)

  case class Moon(location: Location, velocity: Velocity)
  case class Moon1D(location: Int, velocity: Int)

  def init(x: Int, y: Int, z: Int): Moon = {
    Moon(
      Location(x, y, z),
      Velocity(0, 0, 0),
    )
  }

  //    <x=16, y=-8, z=13>
  //    <x=4, y=10, z=10>
  //    <x=17, y=-5, z=6>
  //    <x=13, y=-3, z=0>
  val initialState3D = Seq(
    init(16, -8, 13),
    init(4, 10, 10),
    init(17, -5, 6),
    init(13, -3, 0),
  )

  def velShift(moon: Moon, otherMoon: Moon) = {
    Velocity(
      compare(moon.location.x, otherMoon.location.x),
      compare(moon.location.y, otherMoon.location.y),
      compare(moon.location.z, otherMoon.location.z),
    )
  }

  def compare(a: Int, b: Int) = {
    if (a > b) -1
    else if (a == b) 0
    else 1
  }

  def nextPhase(moons: Seq[Moon]) = {
    val vels = moons.map { moon =>
      moons.filterNot(_ == moon).map { otherMoon =>
        velShift(moon, otherMoon)
      }.foldLeft(moon.velocity)((agg, thisV) => {
        Velocity(
          agg.x + thisV.x,
          agg.y + thisV.y,
          agg.z + thisV.z,
        )
      })
    }
    moons.map(_.location).zip(vels).map {
      case (location, vel) =>
        Moon(
          Location(
            location.x + vel.x,
            location.y + vel.y,
            location.z + vel.z,
          ),
          vel
        )
    }
  }

  def nextPhase1D(moons: Seq[Moon1D]) = {
    val vels = moons.map { moon =>
      moons.filterNot(_ == moon).map { otherMoon =>
        compare(moon.location, otherMoon.location)
      }.foldLeft(moon.velocity)((agg, thisV) => {
        agg + thisV
      })
    }
    moons.map(_.location).zip(vels).map {
      case (location, vel) =>
        Moon1D(
          location + vel,
          vel
        )
    }
  }

  def totalEnergy(system: Seq[Moon]) = {
    system.map { moon =>
      BigInt(Math.abs(moon.location.x) + Math.abs(moon.location.y) + Math.abs(moon.location.z)) *
      BigInt(Math.abs(moon.velocity.x) + Math.abs(moon.velocity.y) + Math.abs(moon.velocity.z))
    }.sum
  }

  lazy val states: LazyList[Seq[Moon]] = makeStream(initialState3D, nextPhase)

  def as1d(moons: Seq[Moon]): Seq[Seq[Moon1D]] = {
    Seq(
      moons.map(moon => {
        Moon1D(moon.location.x, moon.velocity.x)
      }),
      moons.map(moon => {
        Moon1D(moon.location.y, moon.velocity.y)
      }),
      moons.map(moon => {
        Moon1D(moon.location.z, moon.velocity.z)
      })
    )
  }

  def as3d(moonsXYZ: Seq[Seq[Moon1D]]): Seq[Moon] = {
    val dimX = moonsXYZ(0)
    val dimY = moonsXYZ(1)
    val dimZ = moonsXYZ(2)

    zip3(dimX, dimY, dimZ).map {
      case (x, y, z) => Moon(Location(x.location,y.location,z.location), Velocity(x.velocity,y.velocity,z.velocity))
    }
  }

  def zip3[T](s1: Seq[T], s2: Seq[T], s3: Seq[T]): Seq[(T, T, T)] = {
    (0 until s1.length).map { idx =>
      (s1(idx), s2(idx), s3(idx))
    }
  }

  def makeStream[T](initialState: T, transformer: T => T): LazyList[T] = {
    lazy val oneDimensionalProgress: LazyList[T] = initialState #:: oneDimensionalProgress.map(transformer)
    oneDimensionalProgress
  }

  println(totalEnergy(states(1000)))

  //  PART II
  //  maybe more efficient using factorization and accumulating highest power of each prime, but this is good enough.
  def lowestCommonMultiple(xyzCycleLengths: Seq[Int]) = {
    xyzCycleLengths.foldLeft(BigInt(1))((acc, cur) => {
      (acc * cur) / gcd(acc, cur)
    })
  }

  @scala.annotation.tailrec
  def gcd(num1: BigInt, num2: BigInt): BigInt = {
    val max = if (num1 < num2) num2 else num1
    val min = if (num1 < num2) num1 else num2

    if (min == 0) max else gcd(max % min, min)
  }

  def findMinimalCycle(initialState: Seq[Moon]): BigInt = {
    val initialStateByDimension = as1d(initialState)
    val universeProgressByDimension = initialStateByDimension.map(makeStream(_, nextPhase1D))
    val cycleLengthPerDimension = universeProgressByDimension.map(findCycleLength)
    lowestCommonMultiple(cycleLengthPerDimension)
  }

  def findCycleLength[T](l: LazyList[T]): Int = {
    l.tail.indexOf(l.head) + 1
  }

  println(findMinimalCycle(as3d(demo1)))
  println(findMinimalCycle(as3d(demo2)))
  println(findMinimalCycle(initialState3D))
}
