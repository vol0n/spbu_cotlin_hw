package hw8.model

import h8.model.BasicGame
import h8.model.Turn
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
import hw8.SIDE
import hw8.format
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import io.ktor.util.KtorExperimentalAPI
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tornadofx.swap
import tornadofx.toObservable
import tornadofx.getValue
import java.lang.IllegalArgumentException

@Serializable
data class ActivePlayers(val players: List<PlayerInfo>)

@Serializable
data class ClientConfig(val host: String, val port: Int, val baseRoute: String, val getPlayersRoute: String)

@KtorExperimentalAPI
open class WebGame(configName: String = "client_config.json") : BasicGame() {
    private val client = HttpClient {
        install(WebSockets)
        install(JsonFeature) {
           serializer = KotlinxSerializer()
        }
    }

    private val config = Json.decodeFromString<ClientConfig>(
        this::class.java.getResource(configName).readText()
    )

    open val activePlayers = mutableListOf<PlayerInfo>()
    override val players: MutableList<Player> = mutableListOf()

    var onInvite: (Invite) -> Unit = {}
    var onAccept: () -> Unit = {}
    var onDecline: () -> Unit = {}
    var onFail: (String) -> Unit = {}
    var onStart: (PlayerInfo) -> Unit = {}
    var onInviteFail: (String) -> Unit = {}
    private var waitingForReply = false
    private var username = ""
    private val thisPlayer: PlayerInfo by lazy { PlayerInfo(username) }

    private lateinit var session: DefaultClientWebSocketSession

    protected open val incomingInvites = mutableListOf<Invite>()
    open val inComingInvites: List<Invite>
        get() = incomingInvites
    private var outgoingInvite: Invite? = null
    open val outGoingInvite: Invite?
        get() = outgoingInvite
    private var currentGameId: Int? = null

    init {
        onEndGameImpl = { _, _ ->
            runBlocking {
                session.send(format.encodeToString(GameOver() as Message))
            }
        }
    }

    suspend fun setUsername(userName: String): String? {
        if (validationRegex.matches(userName)) {
            val playersOnline = getActivePlayers()
            return when {
                userName.length < minNameLength -> {
                    "username should be at least $minNameLength"
                }
                playersOnline.all { it.name != userName } -> {
                    players.add(LongResponsePlayer(this::resumeWhenResponseReady, userName))
                    username = userName
                    null
                }
                else -> {
                    "This username is occupied!"
                }
            }
        }
        return "Only characters and digits allowed!"
    }

    private suspend fun getActivePlayers(): List<PlayerInfo> {
        val queryString = "http://${config.host}:${config.port}${config.getPlayersRoute}"
        val response: HttpResponse = client.get(queryString)
        return Json.decodeFromString(ActivePlayers.serializer(), response.readText()).players
    }

    suspend fun start() {
        require(username != "") { "Username must be set before starting WebGame!" }
        activePlayers.clear()
        activePlayers.addAll(getActivePlayers())
        client.webSocket(
            method = HttpMethod.Get,
            host = config.host,
            port = config.port,
            path = "${config.baseRoute}/$username",
        ) {
            session = this
            while (true) {
                val otherMessage = incoming.receive() as? Frame.Text ?: continue
                handleIncoming(otherMessage)
            }
        }
        client.close()
        println("Connection closed. Goodbye!")
    }

    suspend fun acceptInvite(invite: Invite) {
        incomingInvites.remove(invite)
        abortGame()
        players.add(WebPlayer(this::resumeWhenResponseReady, invite.sender))
        if (invite.side == SIDE.SenderIsX) players.swap(0, 1)
        session.send(
            format.encodeToString(Reply(invite, RESPONSE.ACCEPT) as Message)
        )
        onStart(invite.recipient)
        currentGameId = invite.id
        play()
    }

    suspend fun declineInvite(invite: Invite) {
        incomingInvites.remove(invite)
        session.send(
            format.encodeToString(Reply(invite, RESPONSE.DECLINE) as Message)
        )
    }

    fun abortGame() {
        if (players.size > 1) {
            players.removeIf { it is WebPlayer }
        }
        runBlocking {
            session.send(format.encodeToString(Disconnected() as Message))
        }
        currentGameId = null
        outgoingInvite = null
        turnNumber = 0
        isPlayable = true
        isGameStarted = false
        winner = null
        winningCombo = null
        clearBoard()
    }

    private fun handleIncoming(frameText: Frame.Text) {
        val message = format.decodeFromString<Message>(
            frameText.readText()
        )
        when (message) {
            is AddPlayer -> {
                println("Handling adding user!")
                message.perform(activePlayers)
            }
            is DeletePlayer -> {
                println("Handling deleting user!")
                message.perform(activePlayers)
            }
            is Invite -> {
                println("Handling invite message!")
                incomingInvites.add(message)
                onInvite(message)
            }
            is Reply -> {
                println("Handling reply!")
                if (message.response == RESPONSE.ACCEPT) {
                    onAccept()
                    currentGameId = outgoingInvite?.id
                    players.add(WebPlayer(this::resumeWhenResponseReady, message.invite.recipient))
                    if (message.invite.side == SIDE.SenderIsO) players.swap(0, 1)
                    onStart(PlayerInfo(currentPlayer.name))
                    play()
                } else {
                    onDecline()
                }
                waitingForReply = false
                println("waiting for reply: $waitingForReply")
            }
            is Move -> {
                println("Handling move!")
                println("IsGameGoing: $isGameGoing")
                println("This gameID: $currentGameId")
                println("gameID from incoming move: ${message.gameID}")
                if (isGameGoing && message.gameID == currentGameId) {
                    println("Current player name: ${currentPlayer.name}")
                    (currentPlayer as? WebPlayer)?.makeTurn(message.turn)
                }
            }
            is Fail -> {
                println("Handling fail!")
                onFail(message.cause)
                runBlocking { abortGame() }
            }
            is CancelInvite -> {
                println("Handling cancel invite!")
                incomingInvites.removeIf { it.sender == message.sender }
                onInviteFail(message.sender.name)
            }
        }
    }

    private inner class WebPlayer(
        callbackWhenReady: (Player, Turn) -> Unit,
        playerInfo: PlayerInfo
    ) : LongResponsePlayer(callbackWhenReady, playerInfo.name)

    suspend fun askToPlay(whom: PlayerInfo, sideStr: String, message: String? = null) {
        println("Asking to play!")
        val side = when (sideStr) {
            "X" -> SIDE.SenderIsX
            "O" -> SIDE.SenderIsO
            else -> throw(IllegalArgumentException("sideStr must be either X or O"))
        }
        outgoingInvite = Invite(thisPlayer, whom, side, hashCode(), message)
        session.send(
            format.encodeToString(outgoingInvite as Message)
        )
        waitingForReply = true
        while (waitingForReply) {
            val otherMessage = session.incoming.poll() as? Frame.Text ?: continue
            handleIncoming(otherMessage)
        }
    }

    suspend fun cancelInvite(recipient: PlayerInfo) {
        if (outGoingInvite?.recipient?.name == recipient.name) {
            outgoingInvite = null
            session.send(
                format.encodeToString(CancelInvite(thisPlayer, recipient) as Message)
            )
        }
    }

    override fun resumeWhenResponseReady(resumingPlayer: Player, turn: Turn) {
        super.resumeWhenResponseReady(resumingPlayer, turn)
        if (currentPlayer is WebPlayer) runBlocking {
            session.send(
                format.encodeToString(Move(turn, currentGameId as Int, currentPlayer.name) as Message)
            )
        }
    }

    fun deliverTurn(turn: Turn) {
        if (currentPlayer !is WebPlayer) {
            (currentPlayer as LongResponsePlayer).makeTurn(turn)
        }
    }

    companion object {
        val validationRegex = "[a-zA-z]+[a-zA-Z1-9]*".toRegex()
        const val minNameLength = 3
    }
}

@KtorExperimentalAPI
class WebGameModel : WebGame() {
    override val activePlayers = super.activePlayers.toObservable()

    override val incomingInvites = super.incomingInvites.toObservable()
    private val inComingInvitesProperty = SimpleListProperty(incomingInvites)
    override val inComingInvites: ObservableList<Invite> by inComingInvitesProperty
}
