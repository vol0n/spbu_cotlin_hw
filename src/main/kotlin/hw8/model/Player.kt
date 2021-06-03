package hw8.model

import h8.model.Cell
import h8.model.BasicGame
import h8.model.Turn
import kotlin.random.Random

interface Player {
    fun retrieveTurn(field: List<List<Cell>>): Turn
    val isLongResponse: Boolean
    val name: String
}

class ComputerRandom : Player {
    companion object {
        fun chooseRandomTurn(field: List<List<Cell>>): Turn {
            val candidates = mutableListOf<Turn>()
            field.mapIndexed { i, row ->
                row.mapIndexed { j, cell ->
                    if (cell == Cell.EMPTY) {
                        candidates.add(Turn(rowPos = i, colPos = j))
                    }
                }
            }
            return candidates[Random.nextInt(0, candidates.size)]
        }
    }
    override val isLongResponse = false
    override fun retrieveTurn(field: List<List<Cell>>) = chooseRandomTurn(field)
    override val name = "Computer with random strategy"
}

class ComputerAI(private val game: BasicGame) : Player {
    override val isLongResponse = false
    override fun retrieveTurn(field: List<List<Cell>>): Turn {
        for (combo in game.getCombos()) {
            if (combo.isCompletable()) {
                return combo.getCompletingTurn()
            }
        }
        return ComputerRandom.chooseRandomTurn(field)
    }
    override val name = "Computer with simple AI"
}

open class LongResponsePlayer(private val callBackWhenReady: (Player, Turn) -> Unit, override var name: String = "you") : Player {
    private var turn: Turn? = null
    open fun makeTurn(turnFromOutSource: Turn) {
            turn = turnFromOutSource
            callBackWhenReady(this, turn as Turn)
    }

    override fun retrieveTurn(field: List<List<Cell>>): Turn {
        return turn ?: error("Turn is not ready!")
    }
    override val isLongResponse = true
}
