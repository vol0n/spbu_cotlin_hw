
package test2

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

fun main() = runBlocking {
    val client = HttpClient(CIO)
    val apiKey: String = Json.decodeFromString(
        File(object {}.javaClass.getResource("api-key.json").path).readText()
    )
    val cities: List<String> = Json.decodeFromString(
        File(object {}.javaClass.getResource("cities.json").path)
        .readText()
    )
    for (city in cities) {
        launch {
            val response: String = client
                .request("http://api.openweathermap.org/data/2.5/weather?q=$city" +
                        "&units=metric" +
                        "&appid=$apiKey") {
                    method = HttpMethod.Get
                }
            println("$city: ${"\"temp\":([\\d.]*)".toRegex().find(response)?.groups?.get(1)?.value}")
        }
    }
}
