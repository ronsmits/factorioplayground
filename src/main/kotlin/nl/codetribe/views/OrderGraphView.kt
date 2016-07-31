package nl.codetribe.views

import javafx.scene.layout.BorderPane
import nl.codetribe.models.Order
import nl.codetribe.models.OrderPart
import tornadofx.View

/**
 * Created by ronsmits on 21/05/16.
 */
class OrderGraphView : View() {
    override val root = BorderPane()

    fun updateGraph(order: Order) {
        println("digraph {")
        addLegend(order.totals)
        recurseGraph(orderpart=order.order)
        println("}")
    }

    private fun addLegend(totals: MutableMap<String, Double>) {
        totals.entries.forEach {
            println("${it.key.replace("-", "")} [shape=box label=\"${it.key.replace("-", " ")}\\n${it.value}\"]")
        }
    }

    private fun recurseGraph(parent: OrderPart?=null, orderpart: OrderPart) {
        if(parent!=null){
            println("\t${parent.item.replace("-", "")} -> ${orderpart.item.replace("-", "")} [dir=back]")
        }
        if (orderpart.orders.size > 0) {

            orderpart.orders.forEach { recurseGraph(orderpart, it) }
        }
    }
}
