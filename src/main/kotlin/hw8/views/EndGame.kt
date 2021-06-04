package hw8.views

import javafx.beans.property.SimpleStringProperty
import hw8.app.Styles
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.vbox
import tornadofx.label
import tornadofx.bind
import tornadofx.button
import tornadofx.action
import tornadofx.center

class EndGame : Fragment("Game ended.") {
    val displayedMessage = SimpleStringProperty("")
    override val root = borderpane {
        addClass(Styles.endGame)
        center {
            vbox {
                label {
                    this.bind(displayedMessage)
                }

                button("Ok") {
                    action {
                        close()
                    }
                }
            }
        }
    }
}
