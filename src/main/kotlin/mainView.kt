import com.sun.javafx.geom.RoundRectangle2D
import javafx.geometry.Rectangle2D
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.ScrollPane
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.shape.Rectangle
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
            val box = Box("first", 1)
                    box.draw(gc=canvas.graphicsContext2D)
            Box("second", 3, connectTo = box).draw(80.0, 80.0, gc=canvas.graphicsContext2D)
            Box("third", 3, box).draw(40.0, 80.0, canvas.graphicsContext2D)

        }
    }
}

class Box(val name: String, val amount: Int, val connectTo: Box? = null) : Group(){
    val textfield = Text(name)
    val textLength = Math.ceil(textfield.layoutBounds.width)
    var xPos: Double = 10.0
    var yPos: Double = 10.0

    fun connectPoint() :Pair<Double, Double>{
        return Pair(xPos+(textfield.layoutBounds.width+6)/2, yPos+textfield.layoutBounds.height+6)
    }

    fun draw(x: Double=10.0, y: Double=10.0, gc: GraphicsContext) {
        xPos=x
        yPos=y
        with(gc) {
            textBaseline = VPos.TOP
            strokeRoundRect(x, y, textLength+6, textfield.layoutBounds.height+6, 10.0, 10.0)
            fillText(textfield.text, x+3, y+3)
            if(connectTo!=null){
                beginPath()
                moveTo(x+(textLength+6)/2, y)
                lineTo(connectTo.connectPoint().first, connectTo.connectPoint().second)
                stroke()
            }
        }
    }
}
