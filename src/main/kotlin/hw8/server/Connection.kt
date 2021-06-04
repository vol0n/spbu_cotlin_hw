package hw8.server

import hw8.PlayerInfo
import io.ktor.http.cio.websocket.DefaultWebSocketSession

class Connection(val session: DefaultWebSocketSession, val player: PlayerInfo)
