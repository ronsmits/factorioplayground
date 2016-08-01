package nl.codetribe

/**
 * Created by ronsmits on 01/08/16.
 */
import com.beust.klaxon.*
import nl.codetribe.models.Ingredient
import nl.codetribe.models.Item
import nl.codetribe.models.Recipe

/**
 * Created by ronsmits on 11/05/16.
 */

fun oldreadDB() {

    val root = parse("/pack.json") as JsonObject
    parseItems(root.obj("item") as JsonObject)
    parseRecipes(root.obj("recipe") as JsonObject)
}

private fun parseItems(itemList: JsonObject) {
    itemList.forEach {
        val itemObject = it.value as JsonObject
        val item = Item(itemObject.string("name")!!, itemObject.string("subgroup")!!, itemObject.int("stack_size")!!, "item")
        items.put(item.name, item)
    }
}

private fun parseRecipes(recipeList: JsonObject) {
    recipeList.forEach {
        val recipeObject = it.value as JsonObject
        val recipe = Recipe(recipeObject.string("name")!!, recipeObject.string("type")!!)
        recipes.put(recipe.name, recipe)
        (recipeObject.get("ingredients") as JsonObject).forEach {
            val ingredient = makeOldIngredient(it.value as JsonObject)
            recipe.ingredients.add(ingredient)
        }
    }
}

private fun makeOldIngredient(ingredientObject: JsonObject): Ingredient {
    val name = ingredientObject.string("1") ?: ingredientObject.string("name")
    val amount = ingredientObject.int("2") ?: ingredientObject.int("amount")
    val ingredient = Ingredient(name!!, amount!!.toDouble())
    return ingredient
}

private fun parse(name: String): Any {
    val cls = Parser()
    val inputStream = cls.javaClass.getResourceAsStream(name)!!
    return Parser().parse(inputStream)!!
}