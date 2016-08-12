package nl.codetribe.views

import javafx.collections.FXCollections
import javafx.scene.control.TableView
import nl.codetribe.models.Order
import tornadofx.View
import tornadofx.column

/**
 * Show the materials needed, this is a different view from the @see nl.codetribe.views.OrderTreeView as this has calculated the totals
 * overal, while the nl.codetribe.views.OrderTreeView only shows the totals per item.
 */
class OrderSummaryView : View() {
    override val root = TableView<Map.Entry<String, Double>>()

    init {
        with(root) {
            column("Title", Map.Entry<String, Double>::key)
            column("Amount", Map.Entry<String, Double>::value)
        }
    }

    fun updateTable(order: Order) {

        with(root) {
            items = FXCollections.observableArrayList<Map.Entry<String, Double>>(order.getTotal().entries)
        }

    }
}