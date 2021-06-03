package hw8.client

import hw8.server.Connection
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import java.util.*

class Client {
    val client = HttpClient {
        install(io.ktor.client.features.websocket.WebSockets)
    }

    suspend fun getActivePlayers() {
        client.ws(
            path = "/game/active"
        ) {

        }
    }
}