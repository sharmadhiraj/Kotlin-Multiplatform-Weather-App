package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Api {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private suspend fun getWeather(): Weather {
        return httpClient.get("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,")
            .body<Weather>()
    }

    suspend fun getWeatherMessage(): String =
        try {
            val weather: Weather = getWeather()
            "${weather.current.temperature}°C at ${weather.current.time}"
        } catch (e: Exception) {
            println("Exception during getting the date of the last successful launch $e")
            "Error occurred"
        }
}