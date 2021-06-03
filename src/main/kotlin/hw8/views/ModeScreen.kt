package hw8.views

import hw8.app.Styles
import hw8.controllers.GameController
import hw8.controllers.WebGameController
import io.ktor.util.*
import javafx.geometry.Pos
import javafx.scene.control.Button
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.vbox

@KtorExperimentalAPI
class ModeScreen: View("Choose mode") {
    val gameController: GameController by inject()
    val webGameController: WebGameController by inject()
    override val root = vbox {
        spacing = Styles.spacingBetweenBtns.value
        alignment = Pos.CENTER
        button("Single mode").action {
            gameController.init()
            replaceWith<MenuScreen>()
        }
        button ("Online mode").action {
            webGameController.init()
            replaceWith<LoginScreen>()
        }

        children.filterIsInstance<Button>().addClass(Styles.spacyButton)
    }
}