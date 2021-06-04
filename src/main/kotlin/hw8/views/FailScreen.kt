package hw8.views

import hw8.app.Styles
import tornadofx.Fragment
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.label
import tornadofx.vbox

class FailScreen : Fragment("") {
    val cause: String by param()
    override val root = vbox {
        addClass(Styles.vbox)
        label(cause)
        button("Ok") {
            action {
                replaceWith<MainMenu>()
            }
        }
    }
}
