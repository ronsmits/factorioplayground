import javafx.collections.FXCollections
import javafx.scene.control.TableView
import tornadofx.View
import tornadofx.column

/**
 * Show the materials needed, this is a different view from the @see OrderTreeView as this has calculated the totals
 * overal, while the OrderTreeView only shows the totals per item.
 */
class OrderSummaryView : View() {
    override val root = TableView<Map.Entry<String, Int>>()

    init {
        with(root) {
            column("Title", Map.Entry<String, Int>::key)
            column("Amount", Map.Entry<String, Int>::value)
        }
    }

    fun updateTable(order: Order) {

        with(root) {
            items = FXCollections.observableArrayList<Map.Entry<String, Int>>(order.getTotal().entries)
        }

    }
}