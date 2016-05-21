import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableView
import tornadofx.View
import tornadofx.column
import tornadofx.populate

/**
 * Show all the orderparts (the stuff that needs to be made/gathered in a treetable
 */
class OrderTreeView : View() {
    override val root = TreeTableView<OrderPart>()

    init {
        with(root) {
            column("Item", OrderPart::item)
            column("Amount", OrderPart::amount)
        }
    }

    fun updateTreeTable(part: OrderPart) {
        with(root) {
            root = TreeItem<OrderPart>(part)
            populate { parent -> parent.value.orders }
            openallChildren(root)
        }
    }

    private fun openallChildren(root: TreeItem<OrderPart>) {
        root.isExpanded = true
        root.children.forEach { openallChildren(it) }
    }
}