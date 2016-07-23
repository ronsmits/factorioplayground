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
    rawRecipes.keys().forEach { key -> handleRecipe(key, rawdata) }
//println((rawdata as LuaTable).keys())

    println(context)

}

fun handleRecipe(key: LuaValue?, rawdata: LuaTable) {
    println(key)
    val recipe = rawdata.get("recipe").get(key.toString()) as LuaTable
    recipe.keys().forEach { it -> println("\t$it - ${recipe.get(it)}")  }
    val ingredients = recipe.get("ingredients") as LuaTable
    ingredients.keys().map ({ it -> ingredients.get(it) as LuaTable }).
            map { ing -> makeIngredient(ing) } }

fun makeIngredient(luaTable: LuaTable) {
    println("called")
    luaTable.keys().map { println("\t\t\t$it - ${luaTable.get(it)}") }
}
//ingredients.keys().forEach { it->println("\t\t${ingredients.recipe(it)}") }


fun main(args: Array<String>) {
    readData()
}