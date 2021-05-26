package hw8.model

import hw8.GameModel
import hw8.Turn
import kotlin.random.Random

interface Player {
    fun retrieveTurn(): Turn
    val isLongResponse: Boolean
    val name: String
    var label: String
}

object ComputerRandom : Player {
    override var label = ""
    override val isLongResponse = false
    override fun retrieveTurn(): Turn {
        val candidates = mutableListOf<Turn>()
        GameModel.gameBoard.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                if (cell.value == GameModel.EMPTY) {
                    candidates.add(Turn(rowPos = i, colPos = j))
                }
            }
        }
        return candidates[Random.nextInt(0, candidates.size)]
    }
    override val name = "Computer with random strategy"
}

object ComputerAI : Player {
    override var label = ""
    override val isLongResponse = false
    override fun retrieveTurn(): Turn {
        for (combo in GameModel.getCombos()) {
            if (combo.isCompletable()) {
                return combo.getCompletingTurn()
            }
        }
        return ComputerRandom.retrieveTurn()
    }
    override val name = "Computer with simple AI"
}

class RealPlayer : Player {
    lateinit var turn: Turn
    fun makeTurn(turnFromUI: Turn) {
        turn = turnFromUI
        GameModel.resumeWhenResponseReady()
    }

    override fun retrieveTurn(): Turn {
        return turn
    }

    override var label = ""
    override val isLongResponse = true
    override var name = "you"
}
