package hw8

import hw8.model.ComputerAI
import hw8.model.ComputerRandom
import hw8.model.Player
import hw8.model.RealPlayer
import tornadofx.swap

data class Turn(val rowPos: Int, val colPos: Int)

enum class Cell(val representation: String) {
    EMPTY(""),
    O("O"),
    X("X")
}

abstract class GameModel {
    companion object {
        const val boardSize = 3
        const val numOfPlayers = 2
        val playersLabels = listOf(Cell.X, Cell.O)
    }

    protected abstract val players: MutableList<Player>
    fun changeTheFirstPlayer() {
        if (turnNumber == 0) {
            players.swap(0, 1)
        }
    }

    private val board: List<MutableList<Cell>> = List(boardSize) {
        MutableList(boardSize) { Cell.EMPTY }
    }

    val gameBoard
        get() = board as List<List<Cell>>
    fun getCell(turn: Turn) = board[turn.rowPos][turn.colPos]

    private var turnNumber = 0
    private var isPlayable = true
    val isGameGoing
        get() = isPlayable
    private val currentPlayerIndex: Int
        get() = turnNumber % numOfPlayers

    fun getCurrentPlayer() = players[currentPlayerIndex]
    var winner: Player? = null
    var winningCombo: Combo? = null
    var onEndGame: (Player?, Combo?) -> Unit = { _, _ -> }
    var onTurn: (Player, Turn, String) -> Unit = { _, _, _ -> }

    private val combos = mutableListOf<Combo>()
    fun getCombos() = combos as List<Combo>

    init {
        // horizontal
        for (i in 0 until boardSize) {
            combos.add(Combo(
                List(boardSize) { j -> Turn(i, j) }
            ))
        }

        // vertical
        for (i in 0 until boardSize) {
            combos.add(Combo(
                List(boardSize) { j -> Turn(j, i) }
            ))
        }

        // diagonals
        combos.add(Combo(
            List(boardSize) { i -> Turn(i, i) }
        ))

        combos.add(Combo(
            List(boardSize) { i -> Turn(boardSize - i - 1, i) }
        ))
    }

    fun play() {
        if (!isPlayable) return
        var currentPlayer = players[currentPlayerIndex]
        while (!currentPlayer.isLongResponse) {
            val turn = currentPlayer.retrieveTurn()
            makeTurn(turn)
            if (!isPlayable) return
            currentPlayer = players[currentPlayerIndex]
        }
    }

    inner class Combo(private val combo: List<Turn>) {
        fun isComplete(): Boolean {
            if (getCell(combo[0]) == Cell.EMPTY) return false
            return combo.count { getCell(it) == getCell(combo[0]) } == combo.size
        }

        fun isCompletable(): Boolean {
            if (combo.count { getCell(it) == Cell.EMPTY } != 1) return false
            val indexOfFullCell = combo.indexOfFirst { getCell(it) != Cell.EMPTY }
            return combo.count { getCell(it) == getCell(combo[indexOfFullCell]) } == combo.size - 1
        }

        fun getCompletingTurn(): Turn {
            require(isCompletable())
            return combo.single { getCell(it) == Cell.EMPTY }
        }
    }

    private fun checkState() {
        if (!isPlayable) {
            return
        }

        for (combo in combos) {
            if (combo.isComplete()) {
                isPlayable = false
                winner = getCurrentPlayer()
                winningCombo = combo
            }
        }
        if (turnNumber == boardSize * boardSize - 1) {
            isPlayable = false
        }
        if (!isPlayable) {
            onEndGame(winner, winningCombo)
        }
    }

    private fun makeTurn(turn: Turn) {
        require(board[turn.rowPos][turn.colPos] == Cell.EMPTY) { "This cell is already used!" }
        board[turn.rowPos][turn.colPos] = playersLabels[currentPlayerIndex]
        onTurn(players[currentPlayerIndex], turn, playersLabels[currentPlayerIndex].representation)
        checkState()
        turnNumber++
    }

    fun resumeWhenResponseReady() {
        if (!isPlayable) return
        val turn = players[currentPlayerIndex].retrieveTurn()
        makeTurn(turn)
        play()
    }
}

class SingleModel : GameModel() {
    override val players: MutableList<Player> = mutableListOf(RealPlayer(this), RealPlayer(this))
}

class RandomModel : GameModel() {
    override val players = mutableListOf(RealPlayer(this), ComputerRandom(this))
}

class AIModel : GameModel() {
    override val players = mutableListOf(RealPlayer(this), ComputerAI(this))
}
