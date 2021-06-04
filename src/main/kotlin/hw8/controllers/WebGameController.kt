package hw8.controllers

import com.sun.javafx.binding.BidirectionalContentBinding
import h8.model.Turn
import hw8.Invite
import hw8.PlayerInfo
import tornadofx.Controller
import hw8.model.WebGameModel
import hw8.views.MainMenu
import hw8.views.BoardScreen
import hw8.views.EndGame
import hw8.views.FailScreen
import hw8.views.InviteEditor
import hw8.views.InviteScreen
import hw8.views.OnlineGameView
import io.ktor.util.KtorExperimentalAPI
import javafx.beans.property.SimpleStringProperty
import javafx.concurrent.Task
import kotlinx.coroutines.runBlocking
import tornadofx.View
import tornadofx.onDoubleClick
import tornadofx.runLater
import tornadofx.selectedItem
import tornadofx.whenUndocked
import tornadofx.getValue
import tornadofx.setValue
import tornadofx.toObservable

@KtorExperimentalAPI
class WebGameController : Controller() {
    val usernameStatusProperty = SimpleStringProperty("")
    var usernameStatus: String by usernameStatusProperty

    private val boardScreen: BoardScreen by inject()
    private val mainMenu: MainMenu by inject()
    private val onlineGameView: OnlineGameView by inject()
    private lateinit var game: WebGameModel
    private val tasks = mutableListOf<Task<Unit>>()
    private var currentView: View = mainMenu
    private val openInviteScreens = mutableListOf<InviteScreen>().toObservable()

    fun login(username: String): Boolean {
        runBlocking {
            usernameStatus = game.setUsername(username) ?: ""
            println(usernameStatus)
        }
        return usernameStatus == ""
    }

    fun stopAllTasks() {
        tasks.map { it.cancel() }
        tasks.clear()
    }

    fun abort() {
        game.abortGame()
        boardScreen.resetTiles()
        currentView = mainMenu
    }

    fun askToPlay(who: PlayerInfo, side: String, message: String? = null) {
            runBlocking {
                game.askToPlay(who, side, message)
            }
    }

    fun cancelInvite(recipient: PlayerInfo) {
        runBlocking {
            game.cancelInvite(recipient)
        }
    }

    fun init() {
        game = WebGameModel()
        setupGameEvents()
        bindUIControls()
        boardScreen.resetTiles()
    }

    fun start() {
        runAsync {
            runBlocking {
                game.start()
            }
        }.also { tasks.add(it) }
    }

    private fun setupGameEvents() {
        game.onInvite = {
            if (!game.isGameGoing) {
                runLater {
                    find<InviteScreen>(
                        mapOf(InviteScreen::invite to it)
                    ).apply {
                        openInviteScreens.add(this)
                        openWindow()
                        whenUndocked { openInviteScreens.remove(this) }
                    }
                }
            }
        }

        game.onInviteFail = { sender ->
            runLater {
                openInviteScreens.find {
                    it.invite.sender.name == sender
                }?.close()
            }
        }

        game.onFail = {
            runLater {
                find<FailScreen>(mapOf(FailScreen::cause to it)).openModal()
            }
        }

        game.onEndGame = { player, _ ->
            val endGameMessage = when (player) {
                null -> "It is draw: nobody won!"
                else -> "${player.name} won!"
            }
            runLater {
                val popup = find<EndGame>()
                popup.displayedMessage.value = endGameMessage
                popup.openModal()
            }
        }

        game.onStart = {
            runLater {
                boardScreen.resetTiles()
                currentView.replaceWith<OnlineGameView>()
                currentView = onlineGameView
            }
        }

        boardScreen.tiles.mapIndexed { i, row ->
            row.mapIndexed { j, tile ->
                tile.onClick = {
                    if (game.isGameGoing) {
                        game.deliverTurn(Turn(rowPos = i, colPos = j))
                    }
                }
            }
        }

        game.onTurn = { _, turn, label ->
            boardScreen.tiles[turn.rowPos][turn.colPos].containedText.text = label
        }
    }

    private fun bindUIControls() {
        BidirectionalContentBinding.bind(game.activePlayers, mainMenu.activePlayers)
        BidirectionalContentBinding.bind(mainMenu.invites, game.inComingInvites)

        mainMenu.playersTable.onDoubleClick {
            val who = mainMenu.playersTable.selectedItem ?: return@onDoubleClick
            find<InviteEditor>(mapOf(InviteEditor::who to who)).openWindow()
        }

        mainMenu.invitesTable.onDoubleClick {
            val who = mainMenu.invitesTable.selectedItem ?: return@onDoubleClick
            find<InviteScreen>(
                mapOf(InviteScreen::invite to who)
            ).openWindow()
        }
    }

    fun handleInviteResponse(invite: Invite, response: Boolean) = runAsync {
        runBlocking {
            if (response) {
                game.acceptInvite(invite)
            } else {
                game.declineInvite(invite)
            }
        }
    }
}
