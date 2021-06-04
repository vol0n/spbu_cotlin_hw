package hw8

import h8.model.Turn
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data class PlayerInfo(val name: String)

@Serializable
abstract class Message

@Serializable
data class AddPlayer(val player: PlayerInfo) : Message() {
    fun perform(target: MutableList<PlayerInfo>) {
        target.add(player)
    }
}

@Serializable
data class DeletePlayer(val player: PlayerInfo) : Message() {
    fun perform(target: MutableList<PlayerInfo>) {
        target.remove(player)
    }
}

@Serializable
enum class SIDE(val label: String) {
    SenderIsX("X"), SenderIsO("O")
}

@Serializable
data class Invite(
    val sender: PlayerInfo,
    val recipient: PlayerInfo,
    val side: SIDE,
    val id: Int,
    val message: String? = null,
) : Message()

@Serializable
enum class RESPONSE {
    ACCEPT, DECLINE
}

@Serializable
data class Reply(val invite: Invite, val response: RESPONSE) : Message()

@Serializable
data class Move(val turn: Turn, val gameID: Int, val recipientName: String) : Message()

@Serializable
data class Fail(val cause: String) : Message()

@Serializable
data class CancelInvite(val sender: PlayerInfo, val recipient: PlayerInfo) : Message()

@Serializable
class GameOver : Message()

@Serializable
class Disconnected : Message()

val module = SerializersModule {
    polymorphic(Message::class) {
        subclass(AddPlayer::class)
        subclass(DeletePlayer::class)
        subclass(Invite::class)
        subclass(Reply::class)
        subclass(Move::class)
        subclass(CancelInvite::class)
        subclass(Fail::class)
        subclass(GameOver::class)
        subclass(Disconnected::class)
    }
}

val format = Json {
    serializersModule = module
    prettyPrint = true
    isLenient = true
}
