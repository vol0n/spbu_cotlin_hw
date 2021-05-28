package hw8.controllers

import hw8.GameModel
import hw8.Turn
import hw8.model.ComputerAI
import hw8.model.ComputerRandom
import hw8.model.RealPlayer
import hw8.views.BoardScreen
import hw8.views.EndGame
import hw8.views.MenuScreen
import tornadofx.Controller
import tornadofx.bind
import tornadofx.swap

data class GameMode(val userMenuMessage: String, val name: String)

class GameController : Controller() {
    private val boardScreen: BoardScreen by inject()
    private lateinit var game: GameModel
    companion object {
        val gameModes = listOf(
            GameMode("Play against computer with random strategy", "Random"),
            GameMode("Play against computer with simple AI", "AI"),
            GameMode("Play with yourself", "Single")
        )
    }
    private val menuScreen: MenuScreen by inject()
    fun init() {
        menuScreen.gameModes = gameModes
    }

    fun startNewGame() {
        game = GameModel()
        val realPlayerLabel = menuScreen.getChosenLabel()
        val secondPlayerLabel = if (realPlayerLabel == "O") "X" else "O"
        val players = mutableListOf(
            RealPlayer(game, realPlayerLabel),
            when (menuScreen.getSelectedMode().name) {
                "Random" -> ComputerRandom(game, secondPlayerLabel)
                "AI" -> ComputerAI(game, secondPlayerLabel)
                else -> RealPlayer(game, secondPlayerLabel)
            }
        )
        if (realPlayerLabel == "O") players.swap(0, 1)
        game.addPlayers(players)

        game.onEndGame = { player, _ ->
            val endGameMessage = when(player) {
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
