/**
 * Created by ronsmits on 12/05/16.
 */

fun main(args: Array<String>) {
    oldreadDB()
    val order=Order("logistic-robot", 2)
    order.getTotal()
    println(order.totals)
}

class Order(val item: String, val amount: Int=1) {
    val order = OrderPart(item, amount)
    override fun toString() = "$order $totals"
    val totals = mutableMapOf<String, Int>()

    fun getTotal() : Map<String, Int>{
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

class OrderPart(val item: String, val amount: Int) {
    val orders = mutableListOf<OrderPart>()
    val recipe: Recipe?

    init {
        recipe = recipes.get(item)
        if (recipe != null) {
            recipe.ingredients.forEach {
                orders.add(OrderPart(it.name, amount * it.amount))
            }
        }
    }

    override fun toString() = "$item $amount $orders"

}


open class BaseElement (val name: String, val type: String, val group: String)

class Item(name: String, group: String, val stacksize: Int, type:String="item"):BaseElement(name, type, group) {

    override fun toString(): String = "$name - $stacksize - $group - $type"
}

class Ingredient(val name: String, val amount: Int){
    val recipes = mutableListOf<Ingredient>()

    override fun toString() = "$name - $amount"

}

class Recipe(name: String, type: String="recipe", group: String="undefined") : BaseElement(name, type, group) {
    val ingredients = mutableListOf<Ingredient>()

    override fun toString() = "$name - $type - $group - $ingredients"
}