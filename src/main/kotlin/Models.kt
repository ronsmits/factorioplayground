/**
 * Created by ronsmits on 12/05/16.
 */

fun main(args: Array<String>) {
    oldreadDB()
    val order=Order("logistic-robot", 2.0)
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

    init {
        recipe = recipes[item]
        if (recipe != null) {
            recipe.ingredients.forEach {
                orders.add(OrderPart(it.name, amount * (it.amount / recipe.result_count)))
            }
        }
    }

    override fun toString() = "$item $amount $orders"

}


open class BaseElement (val name: String, val type: String, val group: String)

class Item(name: String, group: String, val stacksize: Int, type:String="item", val icon:String=""):BaseElement(name, type, group) {

    override fun toString(): String = "$name - $stacksize - $group - $type - $icon"
}

class Ingredient(var name: String="", var amount: Double=0.0){
    val recipes = mutableListOf<Ingredient>()

    override fun toString() = "$name - $amount"

}

class Recipe(name: String, type: String="recipe", group: String="undefined") : BaseElement(name, type, group) {
    val ingredients = mutableListOf<Ingredient>()
    var result_count : Double = 1.0

    override fun toString() = "$name - $type - $group - $result_count - $ingredients"
}