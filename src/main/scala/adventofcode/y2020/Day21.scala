package adventofcode.y2020

object Day21 extends App {
  val inputDemo = """mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
                    |trh fvjkl sbzzf mxmxvkd (contains dairy)
                    |sqjhc fvjkl (contains soy)
                    |sqjhc mxmxvkd sbzzf (contains fish)""".stripMargin

  val input = FilesHandler.read2020(21)

  private def parseInput(input: List[String]) = {
    input
      .map(recipe => {
        val parsed = recipe.split(" \\(contains ")
        val ingredients = parsed(0).split(" ")
        val allergens = parsed(1).dropRight(1).split(", ")

        Recipe(ingredients.toList, allergens.toList)
      })
  }

  var recipes = parseInput(input)
  val distinctAllergens = recipes.flatMap(_.allergens).distinct
  val distinctIngredients = recipes.flatMap(_.ingredients).distinct
  println(s"Allergens: $distinctAllergens")

  val allergensLeft = scala.collection.mutable.ArrayBuffer[String](distinctAllergens: _*)
  val found = scala.collection.mutable.HashMap[String, String]()

  def filterSuspiciousIngredients(allergen: String) = {
    recipes
      .filter(_.allergens.contains(allergen))
      .map(_.ingredients)
      .fold[List[String]](distinctIngredients)(_.intersect(_))
  }

  while (allergensLeft.nonEmpty) {
    val allergen = allergensLeft(Math.floor(allergensLeft.length * Math.random()).toInt)
    filterSuspiciousIngredients(allergen) match {
      case List(only) =>
        found += allergen -> only
        allergensLeft.remove(allergensLeft.indexOf(allergen))
        recipes = recipes.map(r => r.copy(ingredients = r.ingredients.filterNot(_ == only)))
      case Nil => throw new RuntimeException("no candidates => no solution")
      case other =>
        println(s"no unique suspect found for $allergen: $other")
    }
  }

  println(recipes.flatMap(_.ingredients).filterNot(found.contains).length)
  println(found.toList.sortBy(_._1).map(_._2).mkString(","))
}

case class Recipe(
                   ingredients: List[String],
                   allergens: List[String],
                 )