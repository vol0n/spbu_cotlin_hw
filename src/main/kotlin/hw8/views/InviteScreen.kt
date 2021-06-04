package hw8.views

import hw8.Invite
import hw8.app.Styles
import hw8.app.Styles.Companion.inviteScreen
import hw8.controllers.WebGameController
import io.ktor.util.KtorExperimentalAPI
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.Fragment
import tornadofx.action
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.button
import tornadofx.hbox
import tornadofx.label
import tornadofx.style
import tornadofx.text
import tornadofx.textflow
import tornadofx.vbox

@KtorExperimentalAPI
class InviteScreen : Fragment() {
    val invite: Invite by param()
    val controller: WebGameController by inject()
    override val root = borderpane {
        addClass(inviteScreen)
        title = "Invite from: ${invite.sender.name}"
        top = label("Hey! The player ${invite.sender.name} wants to play with you!" +
                    "\nAre you in?")

        center = vbox {
            spacing = Styles.spacingBetweenBtns.value
            textflow {
                text("${invite.sender.name} wants to play for \n") {
                    style {
                        fontWeight = FontWeight.EXTRA_BOLD
                    }
                }
                text(invite.side.label) {
                    style {
                        alignment = Pos.CENTER
                        fontSize = Styles.labelSz
                        fontWeight = FontWeight.EXTRA_BOLD
                        fill = Color.GREEN
                    }
                }
            }
            text(invite.message)
        }
        bottom = hbox {
            alignment = Pos.CENTER
            spacing = Styles.spacingBetweenBtns.value
            button("Accept").action {
                controller.handleAccept(invite)
                close()
            }
            button("Decline").action {
                controller.handleDecline(invite)
                close()
            }
        }
    }
}
