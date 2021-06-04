package h8.model

import hw8.model.ComputerAI
import hw8.model.ComputerRandom
import hw8.model.Player
import hw8.model.LongResponsePlayer
import kotlinx.serialization.Serializable

@Serializable
data class Turn(val rowPos: Int, val colPos: Int)

enum class Cell(val representation: String) {
    EMPTY(""),
    O("O"),
    X("X")
}

abstract class BasicGame {
    protected abstract val players: List<Player>

    private val board: List<MutableList<Cell>> = List(boardSize) {
        MutableList(boardSize) { Cell.EMPTY }
    }

    val gameBoard
        get() = board as List<List<Cell>>

    fun getCell(turn: Turn) = board[turn.rowPos][turn.colPos]

    protected var turnNumber = 0
    protected var isPlayable = true
    protected var isGameStarted = false
    open val isGameGoing
        get() = isPlayable && isGameStarted
    protected val currentPlayer: Player
        get() = players[turnNumber % numOfPlayers]

    var winner: Player? = null
    var winningCombo: Combo? = null
    var onEndGame: (Player?, Combo?) -> Unit = { _, _ -> }
    var onTurn: (Player, Turn, String) -> Unit = { _, _, _ -> }
    protected var onEndGameImpl: (Player?, Combo?) -> Unit = { _, _ -> }

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

    open fun play() {
        if (!isPlayable) return
        isGameStarted = true
        while (!currentPlayer.isLongResponse) {
            val turn = currentPlayer.retrieveTurn(board)
            makeTurn(turn)
            if (!isPlayable) return
        }
    }

    // contains a sequence of positions (Turn) that compose winning combo if all Cell are not empty have the same value
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

    // check if somebody won or it is draw, if yes calls onEndGame
    protected fun checkState() {
        if (!isPlayable) {
            return
        }

        for (combo in combos) {
            if (combo.isComplete()) {
                isPlayable = false
                winner = currentPlayer
                winningCombo = combo
            }
        }
        if (turnNumber == boardSize * boardSize - 1) {
            isPlayable = false
        }
        if (!isPlayable) {
            onEndGame(winner, winningCombo)
            onEndGameImpl(winner, winningCombo)
        }
    }

    protected fun makeTurn(turn: Turn) {
        require(board[turn.rowPos][turn.colPos] == Cell.EMPTY) { "This cell is already used!" }
        board[turn.rowPos][turn.colPos] = playersLabels[turnNumber % numOfPlayers]
        onTurn(currentPlayer, turn, playersLabels[turnNumber % numOfPlayers].representation)
        checkState()
        turnNumber++
    }

    protected open fun resumeWhenResponseReady(resumingPlayer: Player, turn: Turn) {
        if (!isGameGoing || currentPlayer != resumingPlayer) return
        makeTurn(turn)
        play()
    }

    protected fun clearBoard() {
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                board[i][j] = Cell.EMPTY
            }
        }
    }

    companion object {
        const val boardSize = 3
        const val numOfPlayers = 2
        val playersLabels = listOf(Cell.X, Cell.O)
    }
}

abstract class LocalGame(uIPlayerLabel: String = "X") : BasicGame() {
    init {
        if (uIPlayerLabel == "O") turnNumber = 1
    }
    fun deliverTurnFromUI(turn: Turn) {
        if (currentPlayer is LongResponsePlayer) {
            (currentPlayer as LongResponsePlayer).makeTurn(turn)
        }
    }
}

class SingleGame(uIPlayerLabel: String = "X") : LocalGame(uIPlayerLabel) {
    override val players: MutableList<Player> = mutableListOf(
        LongResponsePlayer(::resumeWhenResponseReady),
        LongResponsePlayer(::resumeWhenResponseReady)
    )
}

class RandomGame(uIPlayerLabel: String = "X") : LocalGame(uIPlayerLabel) {
    override val players = mutableListOf(LongResponsePlayer(::resumeWhenResponseReady), ComputerRandom())
}

class AIGame(uIPlayerLabel: String = "X") : LocalGame(uIPlayerLabel) {
    override val players = mutableListOf(LongResponsePlayer(this::resumeWhenResponseReady), ComputerAI(this))
}
