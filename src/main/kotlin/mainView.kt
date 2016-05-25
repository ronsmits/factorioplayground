import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import tornadofx.*
import java.util.*

/**
 * Created by ronsmits on 19/05/16.
 */
class mainView : View() {
    override val root = BorderPane()
    var itemToMake by property<String>()
    fun itemToMakeProperty() = getProperty(mainView::itemToMake)
    val orderTreeView: OrderTreeView by inject()
    val orderTableView: OrderSummaryView by inject()
    val orderGraphView : OrderGraphView by inject()


    init {
        title = "Factorio calculator"

        with (root) {
            prefWidth=800.0
            prefHeight=600.0
            top {
                menubar {
                    menu {
                        text = "File"
                        menuitem("Quit") {
                            System.exit(0)
                        }
                    }
                    hbox {
                        textfield().bind(itemToMakeProperty())
                        button {
                            text = "make"
                            setOnAction {
                                println(itemToMake)
                                val make = itemToMake.replace(" ", "-")
                                val order = Order(make)
                                orderTreeView.updateTreeTable(order.order)
                                orderTableView.updateTable(order)
                                orderGraphView.updateGraph(order)
                                buildmap(order)
                            }
                        }
                    }
                }
            }
            center {
                tabpane {

                    tab("Tree", orderTreeView.root) { tabClosingPolicy = UNAVAILABLE }
                    tab("Shopping list", orderTableView.root) { tabClosingPolicy = UNAVAILABLE }
                }
            }
        }
    }

    val drawmap = mutableMapOf<Int, MutableList<Box>>()
    private fun buildmap(order: Order) {
        var longest = 0
        var longestrow = 0
        recurseDrawMap(row = 0, order = order.order)
        drawmap.entries.forEach {
            if(it.value.size>longest) {
                longest = it.value.size
                longestrow = it.key
            }

        }
        println("longest is $longest in row $longestrow")
        var totallength =0.0
        drawmap[longestrow]?.forEach {
            val text = Text(it.name)
            println("index = ${drawmap[longestrow]?.indexOf(it)}")
            println("${it.name} is ${Math.ceil(text.layoutBounds.width)} long")
            totallength+=Math.ceil(text.layoutBounds.width)+50
        }
        println("total length will be $totallength")

    }

    private fun recurseDrawMap(row: Int, order: OrderPart, connectTo: Box?=null) {
        val element = Box(order.item, order.amount, connectTo)
        if(drawmap.containsKey(row)){
            drawmap[row]?.add(element)
        } else {
            drawmap.plusAssign(Pair(row, mutableListOf(element)))
        }
        order.orders.forEach { recurseDrawMap(row+1, it, element) }
    }
}

data class Box (val name : String, val amount : Int, val connectTo : Box?=null) {
    var x : Int = 0
    var y : Int = 0
}
