package hw8.server

import hw8.AddPlayer
import hw8.CancelInvite
import hw8.DeletePlayer
import hw8.Fail
import hw8.Invite
import hw8.Message
import hw8.Move
import hw8.PlayerInfo
import hw8.Reply
import hw8.format
import hw8.model.ActivePlayers
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.LinkedHashSet

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

interface Command {
    fun perform(target: MutableList<PlayerInfo>)
    val data: PlayerInfo
}

@Serializable
data class PlayerName(val name: String)

@Suppress("unused")
fun Application.module() {
    install(WebSockets)
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }


    routing {
        val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
        val invitations = Collections.synchronizedSet<Invite>(LinkedHashSet())

        suspend fun notifyOnFail(name: String, cause: String)  {
            connections.find { it.player.name == name }?.apply {
                session.send(
                    format.encodeToString(Fail(cause) as Message)
                )
            }
        }

        webSocket("/game/{username}") {
            println("Adding user!")
            /*
            val name = call.request.queryParameters["name"]
                ?: return@webSocket send("Connection failed: malformed or absent username", )

             */

            println("Before receive")
            val name = call.parameters["username"] ?: return@webSocket send("Absent or malformed username!")
            println("after name, got name: $name")
            val thisPlayer = PlayerInfo(name)
            println("Before encoding!")
            val encoded = format.encodeToString(AddPlayer(thisPlayer) as Message)
            println(thisPlayer.name)
            println("Encoded player: $encoded")
            println("After receive")
            for (connection in connections) {
                println("In sending new users: ${connection.player.name}")
                connection.session.send(encoded)
            }

            val thisConnection = Connection(this, thisPlayer)
            connections += thisConnection
            println("Connections size: ${connections.size}")

            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val frameText = frame.readText()
                    println("Received new text frame: \n$frameText")
                    when (val message = format.decodeFromString<Message>(frameText)) {
                         is Invite -> {
                            connections.singleOrNull {
                                it.player == message.recipient
                            }?.apply {
                                session.send(frameText)
                            }
                            invitations += message
                        }
                        is Reply -> {
                            val recipient = message.invite.recipient
                            val sender = message.invite.sender
                            connections.find { sender.name == it.player.name }?.session?.send(frameText) ?:
                            notifyOnFail(thisPlayer.name, "Player ${recipient.name} is not online.")
                        }
                        is Move -> {
                            connections.find { message.recipientName == it.player.name }?.session?.send(frameText) ?:
                            notifyOnFail(thisPlayer.name, "Player ${message.recipientName} is not online.")
                        }
                        is CancelInvite -> {
                            connections.find { message.recipient.name == it.player.name }?.session?.send(frameText)
                        }
                        else -> {

                        }
                    }
                }
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
                val messageToSend = format.encodeToString(
                    DeletePlayer(thisPlayer) as Message
                )
                println("In deleting users")
                for (connection in connections) {
                    connection.session.send(messageToSend)
                }
            }
        }
        get("/game/connect") {
            call.respond(Json.encodeToString(ActivePlayers(connections.map { it.player })))
        }
    }
}