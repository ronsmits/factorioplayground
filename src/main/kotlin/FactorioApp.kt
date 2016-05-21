import tornadofx.App
import tornadofx.importStylesheet

/**
 * Created by ronsmits on 19/05/16.
 */
class FactorioApp : App() {
    override val primaryView = mainView::class

    override fun init() {
        super.init()
//        importStylesheet("/material.css")
        readDB()
    }
}