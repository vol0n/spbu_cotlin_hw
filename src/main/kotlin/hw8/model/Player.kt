package hw8.model

import hw8.Cell.EMPTY
import hw8.GameModel
import hw8.Turn
import kotlin.random.Random

interface Player {
    fun retrieveTurn(): Turn
    val isLongResponse: Boolean
    val name: String
}

class ComputerRandom(private val game: GameModel) : Player {
    companion object {
        fun chooseRandomTurn(game: GameModel): Turn {
            val candidates = mutableListOf<Turn>()
            game.gameBoard.mapIndexed { i, row ->
                row.mapIndexed { j, cell ->
                    if (cell == EMPTY) {
                        candidates.add(Turn(rowPos = i, colPos = j))
                    }
                }
            }
            return candidates[Random.nextInt(0, candidates.size)]
        }
    }
    override val isLongResponse = false
    override fun retrieveTurn() = chooseRandomTurn(game)
    override val name = "Computer with random strategy"
}

class ComputerAI(private val game: GameModel) : Player {
    override val isLongResponse = false
    override fun retrieveTurn(): Turn {
        for (combo in game.getCombos()) {
            if (combo.isCompletable()) {
                return combo.getCompletingTurn()
            }
        }
        return ComputerRandom.chooseRandomTurn(game)
    }
    override val name = "Computer with simple AI"
}

class RealPlayer(private val game: GameModel) : Player {
    lateinit var turn: Turn
    fun makeTurn(turnFromUI: Turn) {
        turn = turnFromUI
        game.resumeWhenResponseReady()
    }

    override fun retrieveTurn(): Turn {
        return turn
    }
    override val isLongResponse = true
    override var name = "you"
}
