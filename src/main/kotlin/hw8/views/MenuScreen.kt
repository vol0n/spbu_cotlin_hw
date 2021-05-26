package hw8.views

import hw8.GameModel
import hw8.controllers.GameController
import hw8.app.Styles.Companion.spacyButton
import hw8.model.ComputerAI
import hw8.model.ComputerRandom
import hw8.model.Player
import hw8.model.RealPlayer
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import hw8.app.Styles
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.hbox
import tornadofx.label
import tornadofx.stringBinding
import tornadofx.style
import tornadofx.togglebutton
import tornadofx.togglegroup
import tornadofx.vbox

interface GameMode {
    fun createPlayer(): Player
    fun userMenuMessage(): String
}

object RandomMode : GameMode {
    override fun createPlayer() = ComputerRandom
    override fun userMenuMessage() = "Play against computer with random strategy"
}

object AIMode : GameMode {
    override fun createPlayer() = ComputerAI
    override fun userMenuMessage() = "Play against computer with simple AI"
}

object Single : GameMode {
    override fun createPlayer() = RealPlayer().apply { name = "player 2" }
    override fun userMenuMessage() = "Play with yourself"
}

class MenuScreen : View("Menu") {
    val gameController: GameController by inject()
    val gameModes = listOf(Single, RandomMode, AIMode)
    val realPlayerLabel = SimpleStringProperty("X")

    private var selectedMode: GameMode = gameModes.first()
    override val root = vbox {
        spacing = Styles.spacingBetweenBtns.value
        addClass(Styles.menuScreen)
        label("SIDE") {
            addClass(Styles.sideLabel)
        }
        hbox {
            addClass(Styles.toggleBtns)
            togglegroup {
                togglebutton("X") {
                    realPlayerLabel.bind(
                        selectedProperty().stringBinding { if (it == true) "X" else "O" }
                    )
                }
                togglebutton("O")
            }
        }

        label("MODE") {
            addClass(Styles.modeLabel)
        }

            gameModes.forEach {
                button(it.userMenuMessage()).action {
                    selectedMode = it
                    replaceWith<GameView>()
                }
            }

            children.filterIsInstance<Button>().addClass(spacyButton)
            style {
                alignment = Pos.CENTER
            }
        }

    override fun onDock() {
        super.onDock()
        GameModel.clearPlayersAndRestart()
    }

    override fun onUndock() {
        super.onUndock()
        GameModel.addPlayer(
            RealPlayer().apply { label = realPlayerLabel.value }
        )
        GameModel.addPlayer(
            selectedMode.createPlayer().apply { label = if (realPlayerLabel.value == "O") "X" else "O" }
        )
        gameController.init()
    }
}
