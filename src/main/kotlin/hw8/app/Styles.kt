package hw8.app

import javafx.geometry.Pos
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.em
import tornadofx.px
import tornadofx.c
import tornadofx.box

@Suppress("MagicNumber")
class Styles : Stylesheet() {
   companion object {
       val spacyButton by cssclass()
       val styledText by cssclass()
       val endGame by cssclass()
       val menuScreen by cssclass()
       val modeLabel by cssclass()
       val sideLabel by cssclass()
       val vbox by cssclass()
       val inviteScreen by cssclass()
       val spacingBetweenBtns = 10.px
       val btnWidth = 300.px
       val labelSz = 40.px
       val winHeight = 450.px
       val winWidth = 500.px

       val toggleBtns by cssclass()
       val toggleBtnsFontSz = 20.px
       val spacingBetweenToggleBtns = 40.px

       const val loginScreenWidth = 300.0
       const val loginScreenHeight = 125.0
   }

    init {
        inviteScreen {
            padding = box(10.px)
            spacing = spacingBetweenBtns

            and(button) {
                alignment = Pos.CENTER
            }

            and(label) {
                fontSize = 15.px
            }
        }

        toggleBtns {
            alignment = Pos.CENTER
            fontSize = toggleBtnsFontSz
            spacing = spacingBetweenToggleBtns
        }

        modeLabel {
            fontSize = 3.em
            textFill = c(80, 180, 47, 0.5)
        }

        sideLabel {
            fontSize = 3.em
            textFill = c(175, 47, 47, 0.5)
        }

        vbox {
            button {
                padding = box(spacingBetweenBtns)
                alignment = Pos.CENTER
            }
        }

        menuScreen {
            prefHeight = winHeight
            prefWidth = winWidth
        }

        endGame {
            padding = tornadofx.box(20.px)
            alignment = Pos.CENTER
            spacing = 10.px
        }

        spacyButton {
            padding = box(spacingBetweenBtns)
            spacing = spacingBetweenBtns
            minWidth = btnWidth
            alignment = Pos.CENTER
        }

        styledText {
            fontSize = labelSz
        }
    }
}
