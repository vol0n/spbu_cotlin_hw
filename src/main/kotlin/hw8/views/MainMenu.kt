package hw8.views

import hw8.Invite
import hw8.PlayerInfo
import hw8.app.Styles
import tornadofx.View
import tornadofx.drawer
import tornadofx.readonlyColumn
import tornadofx.tableview
import tornadofx.toObservable

class MainMenu : View("Players online: ") {
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

    override val root = drawer {
        item("Active players", expanded = true) {
            add(playersTable)
        }
        item("Incoming invites") {
            add(invitesTable)
        }
    }

    override fun onDock() {
        primaryStage.width = Styles.winWidth.value
        primaryStage.height = Styles.winHeight.value
        // primaryStage.centerOnScreen()
    }
}
