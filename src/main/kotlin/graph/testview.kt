package graph

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.View
import tornadofx.add
import tornadofx.center

/**
 * Created by ronsmits on 22/05/16.
 */

class testview : View() {
    override val root = BorderPane()

    val canvas = Canvas(640.0, 480.0)
    var gc  : GraphicsContext = canvas.graphicsContext2D
    val font = Font.font("Times New Roman", FontWeight.NORMAL, 16.0)

    fun setText(){

    }

    init {
        gc.font=font
        gc.stroke= Color.BLACK

        gc.fillText("test", 20.0, 20.0)
        with(root) {
            center {
                add(canvas)
            }
        }
    }

}