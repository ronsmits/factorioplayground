import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

/**
 * Created by ronsmits on 23/07/16.
 */

var rawdata = LuaTable()

fun readData() {
    val lualib = File("/Applications/factorio.app/Contents/data/core/lualib/?.lua")
    val datalib = File("/Applications/factorio.app/Contents/data/base/?.lua")
    val packagePath = "${lualib.absolutePath};${datalib.absolutePath}"

    val context = JsePlatform.standardGlobals()


    val input = FactorioApp::class.java.getResourceAsStream("/compat.lua")
    context.load(input, "compat", "t", context).call()


    context.get("package").set("path", packagePath)
    context.load("require 'dataloader'").call()
    context.load("require 'data'").call()
    val data = context.get("data")
    rawdata = data.get("raw") as LuaTable
    val rawRecipes: LuaTable = rawdata.get("recipe") as LuaTable
    val rawItems: LuaTable = rawdata.get("item") as LuaTable
    rawItems.keys().map { key -> handleItem(key, rawItems.get(key) as LuaTable) }
    rawRecipes.keys().forEach { key -> handleRecipe(key, rawdata) }


}

fun  handleItem(key: LuaValue?, rawitem: LuaTable) {
     val item = Item(
             name = rawitem.get("name").tojstring(),
             group = rawitem.get("subgroup").tojstring(),
             type = rawitem.get("type").tojstring(),
             stacksize = rawitem.get("stack_size").toint(),
             icon = rawitem.get("icon").tojstring()
             )
    items.put(item.name, item)
    println(item)
}

fun handleRecipe(key: LuaValue?, rawdata: LuaTable) {
    val rawrecipe = rawdata.get("recipe").get(key) as LuaTable
    val recipe = Recipe(
            name = rawrecipe.get("name").toString(),
            type = rawrecipe.get("type").tojstring()
    )
    val ingredients = rawrecipe.get("ingredients") as LuaTable
    ingredients.keys().map({ it -> ingredients.get(it) as LuaTable }).
            map { ing -> makeIngredient(recipe, ing) }
    val results = rawrecipe.get("results")
    if (results != LuaValue.NIL) {
        (results as LuaTable).keys().forEach { table ->
            (results.get(table) as LuaTable).keys().forEach { entry ->
                if (entry.tojstring() == "amount") {
                    recipe.result_count = results.get(table).get(entry).todouble()
                }
            }
        }
    } else {
        recipe.result_count = 1.0
    }
    recipes.put(recipe.name, recipe)
    //println(recipe)
}

fun makeIngredient(recipe: Recipe, luaTable: LuaTable) {
    val ingredient = Ingredient()
    luaTable.keys().forEach {
        if (it.toString() == "1" || it.toString() == "name") ingredient.name = luaTable.get(it).toString()
        if (it.toString() == "2" || it.toString() == "amount") ingredient.amount = luaTable.get(it).todouble()
    }
    recipe.ingredients.add(ingredient)
}

fun main(args: Array<String>) {
    readData()
    println(recipes.keys.toList())
}