package nl.codetribe

import nl.codetribe.FactorioApp
import nl.codetribe.items
import nl.codetribe.models.Ingredient
import nl.codetribe.models.Item
import nl.codetribe.models.Order
import nl.codetribe.models.Recipe
import nl.codetribe.recipes
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

/**
 * Created by ronsmits on 23/07/16.
 */

var rawdata = LuaTable()

fun readData() {
    val os = System.getProperty("os.name")
    println(os)
    var packagePath: String
    when (os) {
        "Mac OS X" -> {
            val lualib = File("/Applications/factorio.app/Contents/data/core/lualib/?.lua")
            val datalib = File("/Applications/factorio.app/Contents/data/base/?.lua")
            packagePath = "${lualib.absolutePath};${datalib.absolutePath}"
        }
        "Linux" -> {
            val lualib = File("${System.getProperty("user.home")}/factorio/data/core/lualib/?.lua")
            val datalib = File("${System.getProperty("user.home")}/factorio/data/base/?.lua")
            packagePath = "${lualib.absolutePath};${datalib.absolutePath}"
        }
        else -> packagePath = ""
    }


    val context = JsePlatform.standardGlobals()


    val input = FactorioApp::class.java.getResourceAsStream("/compat.lua")
    context.load(input, "compat", "t", context).call()


    context.get("package").set("path", packagePath)
    context.load("require 'dataloader'").call()
    context.load("require 'data'").call()
    val data = context.get("data")
    rawdata = data.get("raw") as LuaTable
//    (nl.codetribe.getRawdata.get("technology") as LuaTable).keys().forEach { println("tech $it") }
    val ass = rawdata.get("assembling-machine") as LuaTable
    ass.keys().map { key -> handleMachine(ass.get(key) as LuaTable) }
    //(ass.get("assembling-machine-1") as LuaTable).keys().forEach { println("$it - ${(ass.get("assembling-machine-1")as LuaTable).get(it)}") }
    val rawRecipes: LuaTable = rawdata.get("recipe") as LuaTable
    val rawItems: LuaTable = rawdata.get("item") as LuaTable
    rawItems.keys().map { key -> handleItem(key, rawItems.get(key) as LuaTable) }
    rawRecipes.keys().forEach { key -> handleRecipe(key, rawdata) }


    println(items.size)
}

fun handleMachine(machine: LuaTable) {
    println("working for ${machine.get("name")}")
    machine.keys().forEach { println("\t$it - ${machine.get(it)}") }
    val luaTable = machine.get("crafting_categories") as LuaTable
    luaTable.keys().forEach { println("\t\t$it - ${luaTable.get(it)}") }
}

fun handleItem(key: LuaValue?, rawitem: LuaTable) {
//    if(rawitem.get("name").tojstring()=="inserter") rawitem.set("name", "basic-inserter")
    //rawitem.keys().forEach { println("key $it - ${rawitem.get(it)}") }
    val item = Item(
            name = rawitem.get("name").tojstring(),
            group = rawitem.get("subgroup").tojstring(),
            type = rawitem.get("type").tojstring(),
            stacksize = rawitem.get("stack_size").toint(),
            icon = rawitem.get("icon").tojstring()
    )
    items.put(item.name, item)
}

fun handleRecipe(key: LuaValue?, rawdata: LuaTable) {
    val rawrecipe = rawdata.get("recipe").get(key) as LuaTable
    //println(rawrecipe.get("name"))
    //rawrecipe.keys().forEach { println("  recipe key $it - ${rawrecipe.get(it)}") }
    val recipe = Recipe(
            name = rawrecipe.get("name").toString(),
            type = rawrecipe.get("type").tojstring(),
            time = rawrecipe.get("energy_required").toint(),
            craftcategory = rawrecipe.get("category").tojstring()
    )
    val ingredients = rawrecipe.get("ingredients") as LuaTable
    ingredients.keys().map({ it -> ingredients.get(it) as LuaTable }).
            map { ing -> makeIngredient(recipe, ing) }
    val results = rawrecipe.get("results")
    if (results != LuaValue.NIL) {
        (results as LuaTable).keys().forEach { table ->
            (results.get(table) as LuaTable).keys().forEach { entry ->
                run {
                    if (entry.tojstring() == "amount") {
                        recipe.result_count = results.get(table).get(entry).todouble()
                    }
                    if(entry.tojstring() == "type") {
                        recipe.result_type=results.get(table).get(entry).tojstring()
                    }
                }
            }
        }
    } else {
        recipe.result_count = if (rawrecipe.get("result_count").isnil()) 1.0 else rawrecipe.get("result_count").todouble()
    }
    //println(recipe)
    recipes.put(recipe.name, recipe)
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

    val order = Order("effectivity-module", 1.toDouble())
    //println(order)
}