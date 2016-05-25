import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.ScrollPane
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import tornadofx.*

/**
 * Created by ronsmits on 19/05/16.
 */
class mainView : View() {
    override val root = BorderPane()
    var itemToMake by property<String>()
    fun itemToMakeProperty() = getProperty(mainView::itemToMake)
    val orderTreeView: OrderTreeView by inject()
    val orderTableView: OrderSummaryView by inject()
    val orderGraphView: OrderGraphView by inject()
    val testView : TestView by inject()


    init {
        title = "Factorio calculator"

        with (root) {
            prefWidth = 800.0
            prefHeight = 600.0
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
                    tab("test", testView.root) { tabClosingPolicy = UNAVAILABLE }
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
            if (it.value.size > longest) {
                longest = it.value.size
                longestrow = it.key
            }

        }
        println("longest is $longest in row $longestrow")
        var totallength = 0.0
        drawmap[longestrow]?.forEach {
            val text = Text(it.name)
            println("index = ${drawmap[longestrow]?.indexOf(it)}")
            println("${it.name} is ${it.textLength} long")
            totallength += Math.ceil(it.textLength) + 50
        }
        println("total length will be $totallength")

    }

    private fun recurseDrawMap(row: Int, order: OrderPart, connectTo: Box? = null) {
        val element = Box(order.item, order.amount, connectTo)
        if (drawmap.containsKey(row)) {
            drawmap[row]?.add(element)
        } else {
            drawmap.plusAssign(Pair(row, mutableListOf(element)))
        }
        order.orders.forEach { recurseDrawMap(row + 1, it, element) }
    }
}

class TestView : View() {
    override val root = ScrollPane()

    init {
        with(root){

            val canvas = Canvas(800.0,600.0)
            content=canvas
            Box("test", 1).draw(canvas.graphicsContext2D)

        }
    }
}

data class Box(val name: String, val amount: Int, val connectTo: Box? = null) {
    val textfield = Text(name)
    val textLength = Math.ceil(textfield.layoutBounds.width)
    var x: Double = 10.0
    var y: Double = 10.0

    fun draw(gc: GraphicsContext) {
        with(gc) {
            strokeRect(x, y, textLength + 10, textfield.layoutBounds.height + 10)
            fillText(textfield.text, x + 5, y + 10)
        }
    }
}
