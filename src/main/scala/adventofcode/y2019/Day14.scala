package adventofcode.y2019

import scala.collection.mutable

object Day14 extends App {

  val FUEL = "FUEL"
  val ORE = "ORE"

  case class Chemical(amount: BigInt, name: String)

  case class Equation(inputs: Seq[Chemical], output: Chemical)

  def parseInput(str: String) = {
    def parseUnit(x: String) = {
      val unit = x.split(" ")
      val amount = BigInt(unit(0))

      Chemical(amount, unit(1))
    }

    str.split('\n').filter(_.nonEmpty)
      .map(_.split(" => "))
      .map(row => {
        val lhs = row(0)
        val rhs = row(1)

        Equation(
          lhs.split(", ").map(parseUnit),
          parseUnit(rhs)
        )
      })
  }

  def invert(equations: Seq[Equation]) = {
    equations.map { eq =>
      eq.output.name -> Equation(eq.inputs, eq.output)
    }.to(mutable.Map)
  }

  def divCeil(n: BigInt, d: BigInt) = {
    if (n % d == 0)
      n / d
    else
      n / d + 1
  }

  def min(a: BigInt, b: BigInt) = {
    if (a < b) a else b
  }

  def processInput(input: String) = {
    invert(parseInput(input))
  }

  def multiplyEquation(equation: Equation, N: BigInt): Equation = {
    equation.copy(
      inputs = equation.inputs.map(ch => ch.copy(ch.amount * N)),
      output = equation.output.copy(equation.output.amount * N)
    )
  }

  def iterateFuel(equations: Map[String, Equation], oreQuota: BigInt = BigInt("1000000000000")) = {
    val rules = equations.filterNot(_._1 == FUEL)
    val fuelBaseEquation = equations(FUEL)

    var ores = BigInt(0)
    var multiplier = BigInt(1)
    var bestGuessBelowTarget = multiplier

    while (ores < oreQuota) {
      bestGuessBelowTarget = multiplier
      multiplier = multiplier * 2
      ores = solve(rules + (FUEL -> multiplyEquation(fuelBaseEquation, multiplier)))
    }

    var bestGuessAboveTarget = multiplier

    while (bestGuessAboveTarget - bestGuessBelowTarget > 1) {
      multiplier = (bestGuessAboveTarget + bestGuessBelowTarget) / 2
      ores = solve(rules + (FUEL -> multiplyEquation(fuelBaseEquation, multiplier)))

      if (ores > oreQuota)
        bestGuessAboveTarget = multiplier
      else
        bestGuessBelowTarget = multiplier
    }

    bestGuessBelowTarget
  }

  def solve(equations: Map[String, Equation]) = {
    var chemicals = Seq(Chemical(1, FUEL))
    val stock = mutable.Map[String, BigInt]()
    var ores = BigInt(0)

    while (chemicals.nonEmpty) {
      val (diffused, chemicalsToDiffuse) = chemicals.partition(_.name == ORE)
      ores += diffused.map(_.amount).sum
      chemicals = chemicalsToDiffuse.flatMap { chemical =>

        val amountToProduce = chemical.amount
        //  use stock first
        val amountInStock = stock.getOrElse(chemical.name, BigInt(0))
        val stockUsage = min(amountToProduce, amountInStock)

        val amountToDiffuse = amountToProduce - stockUsage

        //  diffuse  new
        val equation = equations(chemical.name)
        val multiplier = divCeil(amountToDiffuse, equation.output.amount)
        val diffusionResult = multiplyEquation(equation, multiplier)
        //  stash leftovers (minus usage)
        val leftoverAmount = diffusionResult.output.amount - amountToDiffuse
        stock.put(chemical.name, amountInStock - stockUsage + leftoverAmount)

        diffusionResult.inputs
      }
    }
    ores
  }

  import Day14Inputs._

  assert(solve(processInput(demo1).toMap) == 165)
  assert(solve(processInput(demo2).toMap) == 13312)
  assert(solve(processInput(demo3).toMap) == 180697)
  assert(solve(processInput(demo4).toMap) == 2210736)

  println(solve(processInput(input).toMap))

  assert(iterateFuel(processInput(demo2).toMap) == 82892753)
  assert(iterateFuel(processInput(demo3).toMap) == 5586022)
  assert(iterateFuel(processInput(demo4).toMap) == 460664)

  println(iterateFuel(processInput(input).toMap)) // 1122036

}

object Day14Inputs {
  val demo1 =
    """
      |9 ORE => 2 A
      |8 ORE => 3 B
      |7 ORE => 5 C
      |3 A, 4 B => 1 AB
      |5 B, 7 C => 1 BC
      |4 C, 1 A => 1 CA
      |2 AB, 3 BC, 4 CA => 1 FUEL
      |""".stripMargin

  val demo2 =
    """
      |157 ORE => 5 NZVS
      |165 ORE => 6 DCFZ
      |44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
      |12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
      |179 ORE => 7 PSHF
      |177 ORE => 5 HKGWZ
      |7 DCFZ, 7 PSHF => 2 XJWVT
      |165 ORE => 2 GPVTF
      |3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT
      |""".stripMargin

  val demo3 =
    """
      |2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG
      |17 NVRVD, 3 JNWZP => 8 VPVL
      |53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
      |22 VJHF, 37 MNCFX => 5 FWMGM
      |139 ORE => 4 NVRVD
      |144 ORE => 7 JNWZP
      |5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC
      |5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV
      |145 ORE => 6 MNCFX
      |1 NVRVD => 8 CXFTF
      |1 VJHF, 6 MNCFX => 4 RFSQX
      |176 ORE => 6 VJHF
      |""".stripMargin

  val demo4 =
    """
      |171 ORE => 8 CNZTR
      |7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
      |114 ORE => 4 BHXH
      |14 VRPVC => 6 BMBT
      |6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
      |6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
      |15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
      |13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
      |5 BMBT => 4 WPTQ
      |189 ORE => 9 KTJDG
      |1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
      |12 VRPVC, 27 CNZTR => 2 XDBXC
      |15 KTJDG, 12 BHXH => 5 XCVML
      |3 BHXH, 2 VRPVC => 7 MZWV
      |121 ORE => 7 VRPVC
      |7 XCVML => 6 RJRHP
      |5 BHXH, 4 VRPVC => 5 LTCX
      |""".stripMargin

  val input =
    """
      |18 FHWM => 1 XGQNZ
      |4 FVPWN, 9 CQGW => 7 QGHT
      |22 KZMS, 1 DMCJL => 8 TWGCK
      |1 LMGQN, 1 DSWDM, 1 GKGZ => 1 TGPH
      |22 WCSW => 1 LVTG
      |13 JSWR => 4 GKGZ
      |162 ORE => 3 FVPWN
      |59 CQGW, 15 MSNG, 6 XGKRF, 10 LJRQ, 1 HRKGV, 15 RKVC => 1 FUEL
      |5 DMCJL => 1 QBLH
      |2 XDRJ, 2 RKVC => 8 CTCNL
      |1 QXHX => 5 GFPSK
      |22 QGHT, 6 GFPSK, 5 DHTPL => 3 CSDR
      |4 QGHT, 2 HFXD => 4 XDRJ
      |10 WQCGV, 1 JSWR, 21 RHTLN => 7 VTPC
      |11 CQGW, 1 FVPWN => 3 HFXD
      |5 VTPC => 2 NCXW
      |8 LDZVS => 6 DQLH
      |117 ORE => 2 KWZNB
      |3 TGPH, 1 JPFQ, 2 WHWLK, 5 RKVC, 16 DQLH => 9 LJRQ
      |14 KWZNB, 2 CQGW => 8 MLPK
      |6 LDZVS => 2 JSWR
      |1 RKVC, 8 HCGT, 9 DHTPL => 6 FHWM
      |3 DHTPL, 1 HWSR, 36 LDZVS => 6 DSWDM
      |5 WHWLK, 1 LJHWT, 8 HSTHS => 7 VMPX
      |22 ZJCDZ, 3 WQCGV => 5 DHTPL
      |10 LJHWT, 32 GFPSK, 2 RHTLN => 4 HFRMP
      |2 FKVD, 3 TWGCK, 1 HWSR => 1 RNLZW
      |2 CSDR, 3 DQLH, 2 HSTHS => 9 JPFQ
      |1 JSWR, 1 PCWS, 1 HFRMP => 3 XGKRF
      |2 QGHT, 9 LVTG, 3 QBLH => 7 RHTLN
      |10 LJHWT, 4 CTCNL => 8 QXHX
      |16 MLPK, 1 HFXD => 9 ZJCDZ
      |6 QGHT => 9 WCSW
      |4 HWSR, 4 MLPK, 1 KZMS => 3 BGZHQ
      |12 MLPK => 8 RKVC
      |1 HWSR, 1 VNWFS => 7 BGFJ
      |7 FHWM, 11 CTDF, 1 LDZVS => 2 VNWFS
      |4 CTDF => 4 HSTHS
      |2 ZJCDZ => 6 LJHWT
      |1 VMPX, 1 NCXW, 1 HSTHS, 41 XGKRF, 30 HLNG, 1 GKGZ => 7 HRKGV
      |1 XGQNZ, 10 PCWS, 3 BGFJ => 8 FKVD
      |1 GFPSK, 1 DMCJL, 1 LVTG => 5 XDTZB
      |3 WCSW => 5 KZMS
      |6 TWGCK, 1 QXHX, 4 BGFJ => 2 LMGQN
      |1 WCSW => 7 LDZVS
      |1 XDTZB, 9 VNWFS => 3 WHWLK
      |3 HFXD, 4 WCSW, 1 MLPK => 5 WQCGV
      |2 BGFJ, 1 HSTHS, 22 MDCB, 10 HWSR, 6 RNLZW, 8 GKGZ => 5 MSNG
      |4 QGHT, 1 FKVD => 7 MDCB
      |9 MLPK, 3 LJHWT => 7 DMCJL
      |121 ORE => 2 CQGW
      |9 DHTPL, 2 BGZHQ => 8 CTDF
      |2 JSWR, 30 RHTLN => 7 HLNG
      |2 QBLH => 7 PCWS
      |14 LVTG => 8 HWSR
      |7 DMCJL => 1 HCGT
      |""".stripMargin
}
