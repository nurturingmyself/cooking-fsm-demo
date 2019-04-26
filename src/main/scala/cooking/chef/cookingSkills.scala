package cooking.chef

import scala.concurrent.Future

sealed trait CookingSkill {
  def cook(ingredients: Ingredients): Future[Food]
}

final case class DistractedNovice() extends CookingSkill {
  private val burnThreshold = 5
  def cook(ingredients: Ingredients): Future[Food] = {
    if (ingredients.servings <= 0) {
      Future.failed(new RuntimeException("Bad ingredients!"))
    } else if (ingredients.servings >= burnThreshold) {
      Future.successful(BurntFood(ingredients.servings))
    } else {
      Future.successful(CookedFood(ingredients.servings))
    }
  }
}
