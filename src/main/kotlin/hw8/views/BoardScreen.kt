package hw8.views

import h8.model.BasicGame
import javafx.geometry.Pos
import tornadofx.View
import tornadofx.gridpane

class BoardScreen : View() {
    var tiles = mutableListOf<List<Tile>>()
    override val root = gridpane {
        alignment = Pos.CENTER
        for (i in 0 until BasicGame.boardSize) {
            val row = mutableListOf<Tile>()
            for (j in 0 until BasicGame.boardSize) {
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
            //it.wasClicked = false
            it.containedText.text = ""
        } }
    }
}
