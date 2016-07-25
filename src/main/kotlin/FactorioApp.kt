import javafx.application.Application
import tornadofx.App

/**
 * Created by ronsmits on 19/05/16.
 */
class FactorioApp : App() {
    override val primaryView = mainView::class

    override fun init() {
        super.init()
//        importStylesheet("/material.css")
        readData()
    }
}

fun main(args: Array<String>) {

    Application.launch(FactorioApp::class.java, *args)
}