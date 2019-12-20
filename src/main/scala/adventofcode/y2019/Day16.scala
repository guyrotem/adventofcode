package adventofcode.y2019

import scala.collection.mutable

object Day16 extends App {
  val basePattern = Seq(0, 1, 0, -1)

  val demo1Input = "80871224585914546619083218645595"
  val inputSignal = "59762677844514231707968927042008409969419694517232887554478298452757853493727797530143429199414189647594938168529426160403829916419900898120019486915163598950118118387983556244981478390010010743564233546700525501778401179747909200321695327752871489034092824259484940223162317182126390835917821347817200000199661513570119976546997597685636902767647085231978254788567397815205001371476581059051537484714721063777591837624734411735276719972310946108792993253386343825492200871313132544437143522345753202438698221420628103468831032567529341170616724533679890049900700498413379538865945324121019550366835772552195421407346881595591791012185841146868209045"

  def repeat(res: Seq[Int], length: Int): Seq[Int] = {
    Seq.fill(length / res.length + 1)(res).flatten.take(length)
  }

  def buildPattern(basePattern: Seq[Int], index: Int, fftLength: Int) = {
    val res = basePattern.flatMap(Seq.fill(index)(_))
    repeat(res, fftLength + 1).slice(1, fftLength + 1)
  }

  def runFft(input: Seq[Int], patterns: Seq[Seq[Int]]): Seq[Int] = {
    assume(input.length == patterns.length, "input and patterns are not aligned. please check.")
    patterns.map { pattern =>
      Math.abs(pattern.zip(input).map {
        case (p, i) => (p * i) % 10
      }.sum) % 10
    }
  }

  def createFftStream(inputSignal: Seq[Int], basePattern: Seq[Int] = Seq(0, 1, 0, -1)) = {
    val fftLength = inputSignal.length
    val allPatterns = (1 to fftLength).map(buildPattern(basePattern, _, fftLength))
    lazy val fftStream: LazyList[Seq[Int]] = inputSignal #:: fftStream.map(runFft(_, allPatterns))
    fftStream
  }

  def parseString(inputAsString: String) = {
    inputAsString.split("").map(_.toInt).toSeq
  }

  println(createFftStream(parseString(inputSignal))(100).take(8).mkString(""))
  //94935919

  //  PART 2
  //
  //  solving is equivalent to multiplying the repeated signal 100 times by the pattern matrix.
  //  pattern matrix for rows > size/2 is all-0 below the diagonal, all-1 on or above it.
  //  for base offsets > size/2 (which is satisfied by our input), effectively we are multiplying by a partial-sum matrix.
  def solveB(inputAsString: String) = {
    val repeatedInput = repeatInputReversedUntilOffset(inputAsString)

    (1 to 100).foreach { _ =>
      partialSums(repeatedInput)
    }

    repeatedInput.reverse.take(8).mkString("")
  }

  private def repeatInputReversedUntilOffset(inputAsString: String) = {
    val messageOffset = inputAsString.take(7).toInt
    val parsedInput = parseString(inputAsString)
    val rawInputLength = inputAsString.length //  before repetition
    val totalLength = rawInputLength * 10000 - messageOffset
    mutable.Seq.fill(totalLength / rawInputLength + 1)(parsedInput.reverse).flatten.take(totalLength)
  }

  def partialSums(nums: mutable.Seq[Int]): Unit = {
    (1 until nums.length).foreach { i =>
      nums(i) = (nums(i - 1) + nums(i)) % 10
    }
  }

  assert(solveB("03036732577212944063491565474664") == "84462026")
  assert(solveB("02935109699940807407585447034323") == "78725270")
  println(solveB(inputSignal))

}
