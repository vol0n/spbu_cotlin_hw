package hw8.views

import hw8.app.Styles
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import tornadofx.addClass
import tornadofx.rectangle

class Tile : StackPane() {
    companion object {
        const val tileSize = 100.0
    }
    // var wasClicked = false
    val containedText = Text("")
    var onClick: () -> Unit = {}
    init {
        rectangle(width = tileSize, height = tileSize) {
            fill = null
            stroke = Color.BLACK
            alignment = Pos.CENTER
        }
        children.add(containedText)
        children.filterIsInstance<Text>().addClass(Styles.styledText)

        setOnMouseClicked {
            if (containedText.text == "") {
                onClick()
            }
        }
    }
}
