import javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import javafx.scene.layout.BorderPane
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
    val orderGraphView : OrderGraphView by inject()

    var order: Order? = null

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
                                order = Order(make)
                                orderTreeView.updateTreeTable((order as Order).order)
                                orderTableView.updateTable((order as Order))
                                orderGraphView.updateGraph((order as Order))
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
}

