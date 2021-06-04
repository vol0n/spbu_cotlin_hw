package hw8.views

import hw8.Invite
import hw8.PlayerInfo
import hw8.app.Styles
import hw8.controllers.WebGameController
import io.ktor.util.KtorExperimentalAPI
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.drawer
import tornadofx.item
import tornadofx.menu
import tornadofx.menubar
import tornadofx.readonlyColumn
import tornadofx.tableview
import tornadofx.toObservable

@KtorExperimentalAPI
class MainMenu : View("TicTacToe online!") {
    val activePlayers = mutableListOf<PlayerInfo>().toObservable()
    val playersTable = tableview(activePlayers) {
        readonlyColumn("Name", PlayerInfo::name)
    }

    val invites = mutableListOf<Invite>().toObservable()
    val invitesTable = tableview(invites) {
        readonlyColumn("Name", Invite::sender)
        readonlyColumn("Side", Invite::side)
        readonlyColumn("Message", Invite::message)
    }

    override val root = borderpane {
        top = menubar {
            menu("Actions") {
                item("Mode menu.").action {
                    replaceWith<ModeScreen>()
                    find<WebGameController>().stopAllTasks()
                }
            }
        }
        center = drawer {
            item("Active players", expanded = true) {
                add(playersTable)
            }
            item("Incoming invites") {
                add(invitesTable)
            }
        }
    }

    override fun onDock() {
        primaryStage.width = Styles.winWidth.value
        primaryStage.height = Styles.winHeight.value
        // primaryStage.centerOnScreen()
    }
}
