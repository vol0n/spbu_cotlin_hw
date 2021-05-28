package hw8.controllers

import hw8.AIModel
import hw8.GameModel
import hw8.RandomModel
import hw8.SingleModel
import hw8.Turn
import hw8.model.RealPlayer
import hw8.views.BoardScreen
import hw8.views.EndGame
import hw8.views.MenuScreen
import tornadofx.Controller

data class GameMode(val userMenuMessage: String, val name: String, val createModel: () -> GameModel)

class GameController : Controller() {
    private val boardScreen: BoardScreen by inject()
    private lateinit var game: GameModel
    companion object {
        val gameModes = listOf(
            GameMode("Play against computer with random strategy", "Random", ::RandomModel),
            GameMode("Play against computer with simple AI", "AI", ::AIModel),
            GameMode("Play with yourself", "Single", ::SingleModel)
        )
    }
    private val menuScreen: MenuScreen by inject()
    fun init() {
        menuScreen.gameModes = gameModes
    }

    fun startNewGame() {
        game = menuScreen.getSelectedMode().createModel()
        if (menuScreen.getChosenLabel() == "O") {
            game.changeTheFirstPlayer()
        }

        game.onEndGame = { player, _ ->
            val endGameMessage = when (player) {
                null -> "It is draw: nobody won!"
                else -> "${player.name} won!"
            }
            val popup = find<EndGame>()
            popup.displayedMessage.value = endGameMessage
            popup.openModal()
        }

        boardScreen.tiles.mapIndexed { i, row ->
            row.mapIndexed { j, tile ->
                tile.onClick = {
                    if (game.isGameGoing) {
                        val c = game.getCurrentPlayer() as RealPlayer
                        c.makeTurn(Turn(rowPos = i, colPos = j))
                    }
                }
            }
        }
        game.onTurn = { _, turn, label ->
            boardScreen.tiles[turn.rowPos][turn.colPos].containedText.text = label
        }
        boardScreen.resetTiles()

        game.play()
    }
}
