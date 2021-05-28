package hw8.app

import hw8.controllers.GameController
import hw8.views.MenuScreen
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class TicTacToeApp : App(MenuScreen::class, Styles::class) {
    private val gameController: GameController by inject()
    override fun start(stage: Stage) {
        stage.minHeight = Styles.winHeight.value
        stage.minWidth = Styles.winWidth.value
        gameController.init()
        super.start(stage)
    }
}

fun main() {
    launch<TicTacToeApp>()
}
