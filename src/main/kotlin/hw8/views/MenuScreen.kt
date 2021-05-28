package hw8.views

import hw8.controllers.GameController
import hw8.app.Styles.Companion.spacyButton
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import hw8.app.Styles
import hw8.controllers.GameMode
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

class MenuScreen : View("Menu") {
    private val gameController: GameController by inject()
    var gameModes = GameController.gameModes

    private val realPlayerLabel = SimpleStringProperty("X")
    fun getChosenLabel(): String = realPlayerLabel.value

    private var selectedMode: GameMode = gameModes.first()
    fun getSelectedMode() = selectedMode

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
            button(it.userMenuMessage).action {
                selectedMode = it
                replaceWith<GameView>()
            }
        }

        children.filterIsInstance<Button>().addClass(spacyButton)
        style {
            alignment = Pos.CENTER
        }
   }

    override fun onUndock() {
        super.onUndock()
        gameController.startNewGame()
    }
}
