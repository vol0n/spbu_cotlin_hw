package hw8.controllers

import h8.model.AIGame
import h8.model.LocalGame
import h8.model.RandomGame
import h8.model.SingleGame
import h8.model.Turn
import hw8.views.BoardScreen
import hw8.views.EndGame
import hw8.views.MenuScreen
import io.ktor.util.*
import tornadofx.Controller

data class GameMode(
    val userMenuMessage: String,
    val name: String,
    val createModel: (String) -> LocalGame
)

@KtorExperimentalAPI
class GameController : Controller() {
    private val boardScreen: BoardScreen by inject()
    private lateinit var localGame: LocalGame
    companion object {
        val gameModes = listOf(
            GameMode("Play against computer with random strategy", "Random", ::RandomGame),
            GameMode("Play against computer with simple AI", "AI", ::AIGame),
            GameMode("Play with yourself", "Single", ::SingleGame),
        )
    }
    private val menuScreen: MenuScreen by inject()
    @KtorExperimentalAPI
    fun init() {
        menuScreen.gameModes = gameModes
    }

    fun startNewGame() {
        localGame = menuScreen.let {
            it.getSelectedMode().createModel(it.getChosenLabel())
        }

        localGame.onEndGame = { player, _ ->
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
                    if (localGame.isGameGoing) {
                        localGame.deliverTurnFromUI(Turn(rowPos = i, colPos = j))
                    }
                }
            }
        }

        localGame.onTurn = { _, turn, label ->
            boardScreen.tiles[turn.rowPos][turn.colPos].containedText.text = label
        }

        boardScreen.resetTiles()

        localGame.play()
    }

}
