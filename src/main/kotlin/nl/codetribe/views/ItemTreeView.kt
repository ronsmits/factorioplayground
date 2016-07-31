package nl.codetribe.views

import javafx.scene.control.TreeItem
import javafx.scene.layout.VBox
import nl.codetribe.items
import nl.codetribe.models.Item
import tornadofx.*

/**
 * Created by ronsmits on 31/07/16.
 */
class ItemTreeView : View(){
    override val root = VBox()

    val groups = items.values.map { it.category }.distinct().map { println(it); Item(it, "", 1) }

    init {
        println(groups)
//        with (root) {
//            treetableview<Item> {
//                root = TreeItem(Item("Items", "", 1))
//                column("name", Item::name)
//                column("category", Item::category)
//
//                populate {parent -> if(parent == root) groups else items.values.filter{it.category == parent.value.name}}
//
//            }
//        }
    }
}