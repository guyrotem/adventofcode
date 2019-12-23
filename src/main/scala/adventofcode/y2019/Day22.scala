package adventofcode.y2019

import scala.collection.mutable

object Day22 extends App {

  import Day22Parsers._

  def runSlow(input: String, numberOfCards: Int) = {
    def shuffleSlow(deck: List[Int], command: Command) = {
      command match {
        case Reverse => deck.reverse
        case DealWithIncrement(n) =>
          val a = mutable.ArraySeq.fill(deck.length)(-1)
          (0 until deck.length).foreach(idx => a.update((idx * n) % deck.length, deck(idx)))
          a.toList
        case Cut(n) =>
          val nn = if (n < 0) deck.length + n else n
          deck.drop(nn) ++: deck.take(nn)
      }
    }

    //  START
    val shuffleCommands = parseInput(input)
    val deck = 0 until numberOfCards

    shuffleCommands.foldLeft(deck.toList)((agg, command) => {
      shuffleSlow(agg, command)
    })
  }

  assert(runSlow(Day22Input.demoX, 10) == List(9, 2, 5, 8, 1, 4, 7, 0, 3, 6))
  //assert(runSlow(Day22Input.input, 10007).indexOf(2019) == 6061)  //  slow...

  //  part II
  case class Deck(
                   size: BigInt,
                   stepSize: BigInt,
                   startOffset: BigInt,
                 ) {
    def unfold: Seq[BigInt] = {
      assert(BigInt(size.toInt) == size)
      (0 until size.toInt).map(idx => (startOffset + stepSize * idx) % size)
    }

    def sampleAt(idx: BigInt): BigInt = {
      (startOffset + stepSize * idx) % size
    }
  }

  def runFast(input: String, numberOfCards: BigInt, rounds: Int = 1) = {
    val shuffleCommands = parseInput(input)
    val initialDeck = Deck(numberOfCards, BigInt(1), BigInt(0))

    (1 to rounds).foldLeft(initialDeck) {
      (deckAtStartOfRound, _) =>
        shuffleCommands.foldLeft(deckAtStartOfRound) {
          (deckStep, command) => {
            shuffleBig(deckStep, command)
          }
        }
    }
  }

  import Day22MathUtils._

  //  it's a clock:
  //    reverse - go 1 step back, multiply step size by -1 (modulo N)
  //    cut -     go N steps (forward or backward)
  //    deal -    multiply step size by 1/n (modulo inverse)
  def shuffleBig(deck: Deck, command: Command) = {
    command match {
      case Reverse =>
        deck.copy(startOffset = normalize(deck.startOffset - deck.stepSize, deck.size), stepSize = deck.size - deck.stepSize)
      case DealWithIncrement(n) =>
        deck.copy(stepSize = (deck.stepSize * clockInverse(n, deck.size)) % deck.size)
      case Cut(n) =>
        deck.copy(startOffset = (deck.startOffset + normalize(n, deck.size) * deck.stepSize) % deck.size)
    }
  }

  assert(clockInverse(3, 10) == 7)
  assert(clockInverse(7, 10) == 3)

  assert(runFast(Day22Input.demoX, 10).unfold == List(9, 2, 5, 8, 1, 4, 7, 0, 3, 6))
  assert(runFast(Day22Input.input, 10007).unfold.indexOf(2019) == 6061)

  //  looking at a small inputs, we can see that stepSize is multiplied by a <constant> factor on every round.
  //  offset is multiplied by same <constant + [some other constant]>
  (1 to 10).map(runFast(Day22Input.demoX, BigInt(13), _)).map(println)

  val NumberOfCards = BigInt("119315717514047")
  val NumberOfRounds = BigInt("101741582076661")

  def solveB(numberOfCards: BigInt, numberOfRounds: BigInt, cardIndex: BigInt) = {

    //  run 2 rounds to extract the constants (any 2 consecutive iterations should work)
    val resultB = (1 to 2).map(runFast(Day22Input.input, numberOfCards, _))
    val mult = resultB(0).stepSize
    val delta = (resultB(0).startOffset * mult - resultB(1).startOffset) % numberOfCards
    val minusDelta = normalize(-delta, numberOfCards)

    //  we can repeat the following for $numberOfRounds (starting at stepSize = 1, startOffset = 0), but it's too slow...
    def next(stepSize: BigInt, startOffset: BigInt) = {
      (stepSize * mult) % numberOfCards -> (startOffset * mult - delta) % numberOfCards
    }

    //  develop it into a formula...
    //  stepSize[n]     = (mult^n) % mod
    //  startOffset[n]  = ... -delta | -delta(1 + mult) | ... = minusDelta * SIGMA[0,n-1](mult^k)
    //                  = [minusDelta * (mult^n - 1) / (mult - 1)] % mod
    //                  = [minusDelta * (mult^n - 1) * [1 / (mult - 1)]] % mod
    val multPowN = expMod(mult, numberOfRounds, numberOfCards)
    //  after $numberOfRounds rounds:
    val stepSizeN = multPowN
    val startOffsetN = (minusDelta * (multPowN - 1) * clockInversePrime(mult - 1, numberOfCards)) % numberOfCards

    //  run the clock for 2020 ticks
    Deck(numberOfCards, stepSizeN, startOffsetN).sampleAt(cardIndex)
  }

  assert(solveB(NumberOfCards, NumberOfRounds, 2020) == BigInt("79490866971571"))
}

object Day22MathUtils {

  def normalize(v: BigInt, l: BigInt): BigInt = (v + l) % l

  def expMod(mult: BigInt, exp: BigInt, mod: BigInt): BigInt = {
    if (exp == BigInt(0)) BigInt(1)
    else if (exp % 2 == 0) expMod((mult * mult) % mod, exp / 2, mod)
    else (mult * expMod(mult * mult, (exp - 1) / 2, mod)) % mod
  }

  //  only works for primes
  def clockInversePrime(a: BigInt, n: BigInt): BigInt = {
    expMod(a, n - 2, n)
  }

  /**
   * find the smallest (or only % mod?) N such that N * k == 1 % mod (1/k in clock arithmetic)
   *
   * @param k
   * @param mod
   * @return
   */
  def clockInverse(k: BigInt, mod: BigInt): BigInt = {
    if (false) {
      //  naïve inverse (slow, but works fine for small numbers)
      var counter = BigInt(0)
      var N = BigInt(0)
      while (N != 1) {
        val advanceBy = (mod - N) / k + 1
        N = (N + (k * advanceBy)) % mod
        counter += advanceBy
      }
      counter
    }

    //  https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm WAT?
    var t = BigInt(0);
    var newt = BigInt(1);
    var r = mod;
    var newr = k;
    var quotient: BigInt = 0
    while (newr != 0) {
      quotient = r / newr
      val (tt, newtt) = (newt, t - quotient * newt)
      val (rr, newrr) = (newr, r - quotient * newr)
      t = tt;
      newt = newtt;
      r = rr;
      newr = newrr;
    }
    if (r > 1)
      throw new RuntimeException("a is not invertible")
    if (t < 0) {
      t = t + mod
    }
    t
    //    for primes, there's a simpler solution:
    //    expMod(k, k-2, mod)
  }
}

object Day22Parsers {
  val commandTemplates = Seq(
    NewStackBuilder, DealWithIncrementBuilder, CutBuilder
  )

  trait CommandBuilder {
    def build(commandString: String): Command
    def matches(commandString: String): Boolean
  }

  object NewStackBuilder extends CommandBuilder {
    val cmd = "deal into new stack"

    override def build(commandString: String): Command = Reverse

    override def matches(commandString: String): Boolean = commandString == cmd
  }

  object DealWithIncrementBuilder extends CommandBuilder {
    val cmd = "deal with increment "
    override def build(commandString: String): Command = {
      DealWithIncrement(
        commandString.replace(cmd, "").toInt
      )
    }

    override def matches(commandString: String): Boolean = commandString.startsWith(cmd)
  }

  object CutBuilder extends CommandBuilder {
    val cmd = "cut "
    override def build(commandString: String): Command = {
      Cut(
        commandString.replace(cmd, "").toInt
      )
    }

    override def matches(commandString: String): Boolean = commandString.startsWith(cmd)
  }

  sealed trait Command

  case object Reverse extends Command
  case class DealWithIncrement(n: Int) extends Command
  case class Cut(n: Int) extends Command

  def parseCommand(command: String): Command = {
    commandTemplates
      .collectFirst {
        case template if template.matches(command) =>
          template.build(command)
      }.getOrElse(throw new RuntimeException(s"[$command]"))
  }

  def parseInput(input: String) = {
    input
      .split('\n')
      .filter(_.nonEmpty)
      .map(parseCommand)
  }
}

object Day22Input {
  val demoX =
    """
      |deal into new stack
      |cut -2
      |deal with increment 7
      |cut 8
      |cut -4
      |deal with increment 7
      |cut 3
      |deal with increment 9
      |deal with increment 3
      |cut -1
      |""".stripMargin

  val input =
    """
      |deal with increment 53
      |cut -619
      |deal with increment 6
      |cut -2911
      |deal with increment 20
      |cut 6986
      |deal into new stack
      |deal with increment 38
      |cut -7609
      |deal with increment 55
      |cut -2390
      |deal into new stack
      |deal with increment 21
      |cut -349
      |deal into new stack
      |deal with increment 62
      |cut -9145
      |deal into new stack
      |cut 1013
      |deal with increment 63
      |cut -4214
      |deal with increment 6
      |cut -9471
      |deal into new stack
      |cut 1966
      |deal with increment 58
      |cut -4382
      |deal with increment 70
      |cut -6132
      |deal into new stack
      |deal with increment 25
      |cut -3962
      |deal with increment 6
      |cut 7401
      |deal with increment 72
      |cut -293
      |deal into new stack
      |cut 4528
      |deal with increment 64
      |cut 6899
      |deal with increment 49
      |cut 310
      |deal with increment 55
      |cut -6735
      |deal into new stack
      |deal with increment 31
      |cut 2368
      |deal with increment 48
      |cut -5602
      |deal with increment 23
      |cut 6410
      |deal with increment 72
      |cut 34
      |deal with increment 51
      |cut 2382
      |deal with increment 31
      |cut 2464
      |deal with increment 38
      |deal into new stack
      |deal with increment 18
      |cut 1764
      |deal with increment 57
      |deal into new stack
      |deal with increment 43
      |cut 8507
      |deal with increment 28
      |cut -3632
      |deal with increment 41
      |cut 8316
      |deal with increment 5
      |cut 610
      |deal with increment 74
      |cut -4956
      |deal with increment 45
      |cut 6518
      |deal with increment 60
      |cut 8750
      |deal with increment 6
      |cut -8411
      |deal with increment 14
      |cut -8300
      |deal with increment 29
      |cut -3297
      |deal with increment 49
      |cut -5261
      |deal with increment 30
      |cut -6595
      |deal into new stack
      |deal with increment 48
      |cut -8193
      |deal with increment 63
      |cut 6595
      |deal with increment 68
      |cut -9468
      |deal into new stack
      |cut -3051
      |deal with increment 3
      |cut -2249
      |deal with increment 9
      |cut -7233
      |""".stripMargin
}