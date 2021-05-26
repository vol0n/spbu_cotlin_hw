package hw8

import hw8.model.Player
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.onChange

data class Turn(val rowPos: Int, val colPos: Int)

object GameModel {
    const val EMPTY = ""
    const val boardSize = 3

    private val board: List<MutableList<SimpleStringProperty>> = List(this.boardSize) { rowIndex ->
        MutableList(this.boardSize) { colIndex ->
            SimpleStringProperty(Turn(rowIndex, colIndex), "cell", EMPTY).apply {
                onChange {
                    checkState()
                }
            }
        }
    }

    val gameBoard
        get() = board as List<List<ReadOnlyStringProperty>>

    private val players = mutableListOf<Player>()
    private var gameStarted = false

    fun addPlayer(player: Player) {
        if (!gameStarted) {
            players.add(player)
        }
    }

    private var turnNumber = 0
    private var isPlayable = true
    val isGameGoing
        get() = isPlayable
    private val currentPlayerIndex: Int
        get() = turnNumber % players.size

    fun getCurrentPlayer() = players[currentPlayerIndex]
    var winner: Player? = null
    var winningCombo: Combo? = null
    var onEndGame: (Player?, Combo?) -> Unit = { _, _ -> }

    private val combos = mutableListOf<Combo>()
    fun getCombos() = combos as List<Combo>

    init {
        // horizontal
        for (i in 0 until this.boardSize) {
            combos.add(Combo(
                List(boardSize) { j -> board[i][j] }
            ))
        }

        // vertical
        for (i in 0 until this.boardSize) {
            combos.add(Combo(
                List(boardSize) { j -> board[j][i] }
            ))
        }

        // diagonals
        combos.add(Combo(
            List(boardSize) { i -> board[i][i] }
        ))

        combos.add(Combo(
            List(boardSize) { i -> board[boardSize - i - 1][i] }
        ))
    }

    fun play() {
        if (!isPlayable) return
        require(players.isNotEmpty()) { "No players, can't play!" }
        gameStarted = true
        var currentPlayer = players[currentPlayerIndex]
        while (!currentPlayer.isLongResponse) {
            val turn = currentPlayer.retrieveTurn()
            makeTurn(turn)
            if (!isPlayable) return
            currentPlayer = players[currentPlayerIndex]
        }
    }

    // for complete restart
    fun clearPlayersAndRestart() {
        restart()
        players.clear()
    }

    // used to play new game with the same players
    fun restart() {
        board.map {
            it.map {
                it.value = EMPTY
            }
        }
        gameStarted = false
        winningCombo = null
        winner = null
        turnNumber = 0
        isPlayable = true
    }

    class Combo(private val cells: List<ReadOnlyStringProperty>) {
        fun isComplete(): Boolean {
            if (cells[0].value == EMPTY) return false
            return cells.count { it.value == cells[0].value } == cells.size
        }

        fun isCompletable(): Boolean {
            if (cells.count { it.value == EMPTY } != 1) return false
            val indexOfFullCell = cells.indexOfFirst { it.value != EMPTY }
            return (cells.count { (it.value == cells[indexOfFullCell].value) } == cells.size - 1)
        }

        fun getCompletingTurn(): Turn {
            require(isCompletable())
            return cells.single { it.value == EMPTY }.bean as Turn
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
        require(board[turn.rowPos][turn.colPos].value == EMPTY) { "This cell is already used!" }
        board[turn.rowPos][turn.colPos].value = players[currentPlayerIndex].label
        turnNumber++
    }

    fun resumeWhenResponseReady() {
        if (!isPlayable) return
        val turn = players[currentPlayerIndex].retrieveTurn()
        makeTurn(turn)
        play()
    }
}
