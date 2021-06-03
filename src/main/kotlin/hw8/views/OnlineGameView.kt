package hw8.views

import hw8.app.Styles
import hw8.controllers.WebGameController
import io.ktor.util.*
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.vbox

@KtorExperimentalAPI
class OnlineGameView: View() {
    val boardScreen: BoardScreen by inject()
    val controller: WebGameController by inject()
    override val root = borderpane {
       center = boardScreen.root
       bottom = vbox {
           spacing = Styles.spacingBetweenBtns.value
           button("Leave").action {
               replaceWith<MainMenu>()
               controller.abort()
           }
       }
    }
}