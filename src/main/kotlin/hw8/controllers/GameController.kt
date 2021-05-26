package hw8.controllers

import hw8.GameModel
import hw8.views.BoardScreen
import hw8.views.EndGame
import tornadofx.Controller

class GameController : Controller() {
    val boardScreen: BoardScreen by inject()

    init {
        GameModel.onEndGame = { player, _ ->
            val resMes = when (player) {
                null -> "It is draw: nobody won!"
                else -> "${player.name} won!"
            }
            val popup = find<EndGame>()
            popup.displayedMessage.value = resMes
            popup.openModal()
        }
    }

    fun init() {
        restart()
        GameModel.play()
    }

    fun restart() {
        boardScreen.resetTiles()
        GameModel.restart()
    }
}
