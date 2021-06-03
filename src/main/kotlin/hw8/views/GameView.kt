package hw8.views

import javafx.scene.control.Button
import hw8.app.Styles
import hw8.controllers.GameController
import io.ktor.util.*
import javafx.geometry.Pos
import tornadofx.View
import tornadofx.borderpane
import tornadofx.button
import tornadofx.vbox
import tornadofx.action
import tornadofx.spacer
import tornadofx.addClass

@KtorExperimentalAPI
class GameView : View() {
    val gameController: GameController by inject()
    val boardScreen: BoardScreen by inject()
    override val root = borderpane {
        center = boardScreen.root
        bottom = vbox {
            alignment = Pos.CENTER
            spacing = Styles.spacingBetweenBtns.value
            button("Play again").action {
                    gameController.startNewGame()
            }
            button("Go to menu.").action {
                replaceWith<MenuScreen>(sizeToScene = true, centerOnScreen = true)
            }
            spacer {}

            children.filterIsInstance<Button>().addClass(Styles.spacyButton)
        }
    }
}
