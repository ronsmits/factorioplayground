package nl.codetribe

import javafx.collections.FXCollections
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.ScrollPane
import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import nl.codetribe.models.Item
import nl.codetribe.models.Order
import nl.codetribe.models.OrderPart
import nl.codetribe.views.ItemTreeView
import tornadofx.*
import nl.codetribe.views.OrderSummaryView
import nl.codetribe.views.OrderTreeView

/**
 * Created by ronsmits on 19/05/16.
 */
class mainView : View() {
    override val root = BorderPane()
    var itemToMake by property<String>()
    fun itemToMakeProperty() = getProperty(mainView::itemToMake)
    var amountToMake by property<Int>()
    fun amountToMakeProperty() = getProperty(mainView::amountToMake)
    val orderTreeView: OrderTreeView by inject()
    val orderTableView: OrderSummaryView by inject()
    val itemtreeview : ItemTreeView by inject()

    init {
        amountToMake = 1
        title = "Factorio calculator"

        with(root) {
            prefWidth = 800.0
            prefHeight = 600.0

            top = vbox {
                menubar {
                    menu {
                        text = "File"
                        menuitem("Quit") {
                            System.exit(0)
                        }
                    }
                    hbox {
                        textfield().bind(itemToMakeProperty())
                        label("amount")
                        textfield().bind(amountToMakeProperty())
                        button {
                            text = "make"
                            setOnAction {
                                println(itemToMake)
                                println(amountToMake)
                                val make = itemToMake.replace(" ", "-")
                                val order = Order(make, amountToMake.toDouble())
                                orderTreeView.updateTreeTable(order.order)
                                orderTableView.updateTable(order)
                            }
                        }
                    }
                }
            }
            center = tabpane {
                        tab("Tree", orderTreeView.root) { tabClosingPolicy = UNAVAILABLE }
                        tab("Shopping list", orderTableView.root) { tabClosingPolicy = UNAVAILABLE }
                        tab("item tree", itemtreeview.root) {tabClosingPolicy = UNAVAILABLE}
                    }
        }
    }
}

