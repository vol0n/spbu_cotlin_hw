package hw8.server

import hw8.AddPlayer
import hw8.CancelInvite
import hw8.DeletePlayer
import hw8.Disconnected
import hw8.Fail
import hw8.GameOver
import hw8.Invite
import hw8.Message
import hw8.Move
import hw8.PlayerInfo
import hw8.RESPONSE
import hw8.Reply
import hw8.format
import hw8.model.ActivePlayers
import hw8.server.ServerState.activeGames
import hw8.server.ServerState.connections
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.websocket.webSocket
import io.ktor.websocket.WebSockets
import io.ktor.http.cio.websocket.send
import java.util.Collections
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.LinkedHashSet
import kotlinx.serialization.Serializable

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Serializable
data class ActiveGame(val players: Set<String>) {
    constructor(player1: String, player2: String) : this(setOf(player1, player2))
    constructor(invite: Invite) : this(setOf(invite.sender.name, invite.recipient.name))
}

object ServerState {
    val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    val activeGames = Collections.synchronizedSet<ActiveGame>(LinkedHashSet())
}

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
        webSocket("/game/{username}") {
            val name = call.parameters["username"] ?: return@webSocket send("Absent or malformed username!")
            val thisPlayer = PlayerInfo(name)

            for (connection in connections) {
                connection.session.send(format.encodeToString(AddPlayer(thisPlayer) as Message))
            }
            val thisConnection = Connection(this, thisPlayer)
            connections += thisConnection

            try {
                for (frame in incoming) {
                    handleIncoming(frame, thisPlayer)
                }
            } finally {
                connections -= thisConnection
                val messageToSend = format.encodeToString(DeletePlayer(thisPlayer) as Message)
                for (connection in connections) { connection.session.send(messageToSend) }
                val opponentName = activeGames.find { it.players.contains(thisPlayer.name) }
                    ?.players?.find { it != thisPlayer.name }
                notifyOnFail(opponentName, "$opponentName has left the game.", connections)
            }
        }

        get("/game/connect") {
            call.respond(Json.encodeToString(ActivePlayers(connections.map { it.player })))
        }
    }
}

suspend fun handleIncoming(frame: Frame, thisPlayer: PlayerInfo) {
    if (frame !is Frame.Text) return
    val frameText = frame.readText()
    when (val message = format.decodeFromString<Message>(frameText)) {
        is Invite -> {
            connections.singleOrNull {
                it.player == message.recipient
            }?.apply {
                session.send(frameText)
            }
        }
        is Reply -> {
            if (message.response == RESPONSE.ACCEPT) {
                activeGames.add(ActiveGame(message.invite))
            }
            connections.find { message.invite.sender.name == it.player.name }?.session?.send(frameText)
                ?: notifyOnFail(message.invite.recipient.name,
                    "Player ${message.invite.recipient.name} is not online.", connections)
        }
        is Move -> {
            connections.find { message.recipientName == it.player.name }?.session?.send(frameText)
                ?: notifyOnFail(message.recipientName, "Player ${message.recipientName} is not online.", connections)
        }
        is CancelInvite -> {
            connections.find { message.recipient.name == it.player.name }?.session?.send(frameText)
        }
        is GameOver -> {
            activeGames.removeIf { it.players.contains(thisPlayer.name) }
        }
        is Disconnected -> {
            activeGames.find { it.players.contains(thisPlayer.name) }?.apply {
                notifyOnFail(this.players.find { it != thisPlayer.name },
                "Player `${thisPlayer.name}` has left the game.", connections)
                activeGames.remove(this)
            }
        }
    }
}

suspend fun notifyOnFail(name: String?, cause: String, connections: Set<Connection>) {
    connections.find { it.player.name == name }?.apply {
        session.send(
            format.encodeToString(Fail(cause) as Message)
        )
    }
}
