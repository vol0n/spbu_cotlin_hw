package test2

import io.ktor.client.*
import io.ktor.client.engine.cio.*


import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

val client = HttpClient(CIO)
fun main() = runBlocking {
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