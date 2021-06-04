package hw8.app

import hw8.controllers.WebGameController
import hw8.views.ModeScreen
import io.ktor.util.KtorExperimentalAPI
import javafx.stage.Stage
import tornadofx.App
import tornadofx.find
import tornadofx.launch

@KtorExperimentalAPI
class TicTacToeApp : App(ModeScreen::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.height = Styles.winHeight.value
        stage.width = Styles.winWidth.value
        super.start(stage)
    }

    override fun stop() {
        find<WebGameController>().stopAllTasks()
        super.stop()
    }
}

@KtorExperimentalAPI
fun main() {
    launch<TicTacToeApp>()
}
