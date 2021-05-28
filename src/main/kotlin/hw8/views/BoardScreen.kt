package hw8.views

import hw8.GameModel
import hw8.Turn
import hw8.controllers.GameController
import hw8.model.RealPlayer
import javafx.geometry.Pos
import tornadofx.View
import tornadofx.bind
import tornadofx.gridpane

class BoardScreen() : View() {
    var tiles = mutableListOf<List<Tile>>()
    override val root = gridpane {
        alignment = Pos.CENTER
        for (i in 0 until GameModel.boardSize) {
            val row = mutableListOf<Tile>()
            for (j in 0 until GameModel.boardSize) {
                Tile().also {
                    row.add(it)
                    this.add(it, i, j)
                }
            }
            tiles.add(row)
        }
    }

    fun resetTiles() {
        tiles.map { row -> row.map {
            it.wasClicked = false
            it.containedText.text = ""
        } }
    }
}
