package hw8.views

import hw8.app.Styles
import hw8.controllers.WebGameController
import io.ktor.util.KtorExperimentalAPI
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.View
import tornadofx.ViewModel
import tornadofx.action
import tornadofx.button
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.hbox
import tornadofx.label
import tornadofx.paddingTop
import tornadofx.style
import tornadofx.textfield
import tornadofx.validator

@KtorExperimentalAPI
class LoginScreen : View("Login") {
    val controller: WebGameController by inject()
    val validationRegex = "[a-zA-z]+[a-zA-Z1-9]*".toRegex()
    private val model = ViewModel()
    val username = model.bind { SimpleStringProperty() }

    init {
        println("loginScreen was created!")
        println(validationRegex.toString())
    }

    override val root: Parent = form {
        closeableWhen { username.isNotEmpty }
        fieldset {
            field("Enter your username") {
                textfield(username) {
                    validator {
                        println(validationRegex.toString())
                        val str = it ?: ""
                        if (str.length < minInputLength) {
                            error("Too few characters")
                        } else if (!validationRegex.matches(str)) {
                            error("Incorrect username: only symbols and digits are allowed!")
                        } else {
                            success("Correct username!")
                        }
                    }
                }
            }
        }
        hbox {
            spacing = Styles.spacingBetweenBtns.value

            button("Ok") {
                isDefaultButton = true
                action {
                    if (controller.login(username.value)) {
                        replaceWith<MainMenu>()
                    }
                }
            }
            label(controller.usernameStatusProperty) {
                style {
                    paddingTop = Styles.spacingBetweenBtns.value
                    textFill = Color.RED
                    fontWeight = FontWeight.BOLD
                }
            }
        }
        }
    }

    override fun onDock() {
        primaryStage.width = Styles.loginScreenWidth
        primaryStage.height = Styles.loginScreenHeight
        // primaryStage.centerOnScreen()
    }

    companion object {
        const val minInputLength = 3
    }
}
