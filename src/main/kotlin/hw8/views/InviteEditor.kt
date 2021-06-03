package hw8.views

import hw8.Invite
import hw8.PlayerInfo
import hw8.app.Styles
import hw8.controllers.WebGameController
import io.ktor.util.*
import javafx.beans.property.SimpleStringProperty
import tornadofx.Fragment
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.hbox
import tornadofx.label
import tornadofx.launch
import tornadofx.px
import tornadofx.runAsyncWithProgress
import tornadofx.runLater
import tornadofx.stackpane
import tornadofx.stringBinding
import tornadofx.textfield
import tornadofx.togglebutton
import tornadofx.togglegroup
import tornadofx.vbox

@KtorExperimentalAPI
class InviteEditor: Fragment("Invite params") {
    val message = SimpleStringProperty("")
    val side = SimpleStringProperty("")
    val who: PlayerInfo by param()
    val controller: WebGameController by inject()
    override val root = form {
        fieldset {
            field("Message: ") {
                textfield(message)
            }
            field("You want to play for: ") {
                hbox {
                    spacing = Styles.spacingBetweenBtns.value
                    togglegroup {
                        togglebutton("X") {
                            side.bind(
                                selectedProperty().stringBinding { if (it == true) "X" else "O" }
                            )
                        }
                        togglebutton("O")
                    }
                }
            }
        }
        hbox {
            spacing = Styles.spacingBetweenBtns.value
            stackpane {
                button("Ok").action {
                    runAsyncWithProgress {
                        controller.askToPlay(who, side.value, message.value)
                    } ui {
                        close()
                    }
                }
            }
            button("Cancel").action {
                controller.cancelInvite(who)
                close()
            }
        }
    }
}