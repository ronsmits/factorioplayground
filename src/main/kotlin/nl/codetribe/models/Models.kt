package nl.codetribe.models

import nl.codetribe.oldreadDB
import nl.codetribe.recipes

/**
 * Created by ronsmits on 12/05/16.
 */

fun main(args: Array<String>) {
    oldreadDB()
    val order= Order("logistic-robot", 2.0)
    order.getTotal()
    println(order.totals)
}

class Order(val item: String, val amount: Double=1.0) {
    val order = OrderPart(item, amount.toDouble())
    override fun toString() = "$order $totals"
    val totals = mutableMapOf<String, Double>()

    fun getTotal() : Map<String, Double>{
        walkTheOrderPart(order)
        return totals
    }

    private fun walkTheOrderPart(order: OrderPart) {
        if(totals[order.item]!=null){
            val newAmount = totals[order.item]!! + order.amount
            totals+=(Pair(order.item, newAmount))
        } else {
            totals[order.item]=order.amount
        }
        //println(totals)
        order.orders.forEach { walkTheOrderPart(it) }
    }

}

class OrderPart(val item: String, val amount: Double) {
    val orders = mutableListOf<OrderPart>()
    val recipe: Recipe?
    var realamount =amount
    init {
        recipe = recipes[item]
        if (recipe != null) {
            recipe.ingredients.forEach {
                if(realamount < recipe.result_count) realamount = recipe.result_count
                orders.add(OrderPart(it.name, realamount * (it.amount / recipe.result_count)))
            }
        }
    }

    override fun toString() = "$item $amount $orders"

}


open class BaseElement (val name: String, val type: String, val category: String)

class Item(name: String, group: String, val stacksize: Int, type:String="item", val icon:String=""): BaseElement(name, type, group) {

    override fun toString(): String = "$name - $stacksize - ${category} - $type - $icon"
}

class Assembler(name: String, group: String, type:String="assembler", val ingredientcount:Int, val speed:Double): BaseElement(name, type, group) {
    override fun toString() = "$name - $type - ${category}"
}

class Ingredient(var name: String="", var amount: Double=0.0){
    val recipes = mutableListOf<Ingredient>()

    override fun toString() = "$name - $amount"

}

class Recipe(name: String, type: String="recipe", group: String="undefined", time : Int=1) : BaseElement(name, type, group) {
    val ingredients = mutableListOf<Ingredient>()
    var result_count : Double = 1.0

    override fun toString() = "$name - $type - ${category} - $result_count - $ingredients"
}