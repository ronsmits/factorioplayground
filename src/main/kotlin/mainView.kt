import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.ScrollPane
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
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
    val testView: TestView by inject()


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
                                testView.updateGraph(order)
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


}


class TestView : View() {
    override val root = ScrollPane()
    val canvas = Canvas(800.0, 600.0)

    init {
        with(root) {
            content = canvas
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

        val horizontalGap = 50.0
        val verticalGap = 75.0
        val center = horizontalGap+totallength/2
        drawmap.entries.forEach {
            println(it)
            val currentRow = it.key
            val rowlength = calculateRowLength(it.value)

            it.value.forEach {
                val index = drawmap[currentRow]?.indexOf(it) ?: 0
                var y = currentRow * verticalGap
                if (y==0.0) y=20.0
                var x = center - rowlength/2
                if (index > 0) {
                    val leftbox = (drawmap[currentRow]?.get(index - 1) as Box)
                    x = leftbox.textLength + leftbox.xPos + 6 + horizontalGap
                }
                it.draw(x, y, canvas.graphicsContext2D).connect(canvas.graphicsContext2D)
            }
        }
    }

    private fun calculateRowLength(value: MutableList<Box>, gap: Double=50.0): Double {
        var totallength:Double=0.0
        value.forEach { totallength+=it.textLength+6+gap }
        totallength-gap
        return totallength
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

    fun updateGraph(order: Order) {
        drawmap.clear()
        canvas.graphicsContext2D.clearRect(0.0, 0.0, canvas.width, canvas.height)
        buildmap(order)
        drawmap[0]?.get(0)?.calculate(10.0, 10.0)

    }
}

class Box(val name: String, val amount: Int, val parent: Box? = null) : Group() {
    val textfield = Text("$name - $amount")
    val textLength: Double
        get() = Math.ceil(textfield.layoutBounds.width)
    val textHeight: Double
        get() = Math.ceil(textfield.layoutBounds.height)
    var xPos: Double = 10.0
    var yPos: Double = 10.0

    private val kids= mutableListOf<Box>()

    init {
        if(parent !=null) {
            parent.kids.add(this)
        }
    }
    /**
     * Return the bottom center of the box. This can be used to draw lines between boxes
     * in the returned Pair <code>first</code> is the x coordinate, <code>second</code> is the y coordinate
     */
    fun connectPoint(): Pair<Double, Double> {
        return Pair(xPos + (textLength + 6) / 2, yPos + textHeight + 6)
    }

    fun calculate(x: Double=50.0, y: Double=50.0) {
        var widthOfChildren = 0.0
        kids.forEach { widthOfChildren+=it.textLength+6+50 }
        println(widthOfChildren)

    }
    fun draw(x: Double = 10.0, y: Double = 10.0, gc: GraphicsContext): Box {

        xPos = x
        yPos = y

        with(gc) {
            textBaseline = VPos.TOP
            strokeRoundRect(x, y, textLength + 6, textHeight + 6, 10.0, 10.0)
            fillText(textfield.text, x + 3, y + 3)
            if(parent?.kids?.size==1) {
               // parent?.redraw(x,  gc)
            }
        }
        return this
    }

    private fun redraw(newX: Double, gc: GraphicsContext) {
        clear(gc)
        draw(newX, yPos, gc)
//        kids.forEach { it.redraw(newX, gc) }
    }
    private fun clear(gc: GraphicsContext): Box {
        gc.clearRect(xPos, yPos, textLength+6, textHeight+7)
        return this
    }

    fun connect(gc: GraphicsContext): Box {
        with (gc) {
            if (parent != null) {
                beginPath()
                var xSource : Double = 0.0
                var ySource : Double = 0.0
                    xSource = xPos + (textLength + 6) / 2
                moveTo(xSource, yPos)
                lineTo(parent.connectPoint().first, parent.connectPoint().second)
                stroke()
            }
        }
        return this
    }
}
