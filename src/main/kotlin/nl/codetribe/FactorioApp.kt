package nl.codetribe

import javafx.application.Application
import nl.codetribe.models.Item
import nl.codetribe.models.Recipe
import tornadofx.App

/**
 * Created by ronsmits on 19/05/16.
 */

val recipes = mutableMapOf<String, Recipe>()
val items = mutableMapOf<String, Item>()
class FactorioApp : App() {
    override val primaryView = mainView::class

    override fun init() {
        super.init()
//        importStylesheet("/material.css")
        oldreadDB()
    }
}

fun main(args: Array<String>) {

    Application.launch(FactorioApp::class.java, *args)
}