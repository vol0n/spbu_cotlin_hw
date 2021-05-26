package hw8.app

import hw8.views.MenuScreen
import javafx.stage.Stage
import hw8.app.Styles
import tornadofx.App
import tornadofx.launch

class TicTacToeApp : App(MenuScreen::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.minHeight = Styles.winHeight.value
        stage.minWidth = Styles.winWidth.value
        super.start(stage)
    }
}

fun main() {
    launch<TicTacToeApp>()
}
