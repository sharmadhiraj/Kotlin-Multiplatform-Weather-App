package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class LocationNotFoundException(val query: String) : Exception("No location found for $query")

interface WeatherRepository {
    suspend fun findLocation(name: String): Location

    suspend fun getWeather(location: Location): Weather
}

fun createHttpClient(): HttpClient = HttpClient {
    expectSuccess = true
    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        connectTimeoutMillis = REQUEST_TIMEOUT_MILLIS
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

class KtorWeatherRepository(
    private val httpClient: HttpClient = createHttpClient(),
) : WeatherRepository {

    override suspend fun findLocation(name: String): Location =
        httpClient.get(GEOCODING_URL) {
            parameter("name", name)
            parameter("count", 1)
        }.body<GeocodingResponse>().results.firstOrNull() ?: throw LocationNotFoundException(name)

    override suspend fun getWeather(location: Location): Weather =
        httpClient.get(FORECAST_URL) {
            parameter("latitude", location.latitude)
            parameter("longitude", location.longitude)
            parameter("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code")
            parameter("daily", "weather_code,temperature_2m_max,temperature_2m_min")
            parameter("timezone", "auto")
            parameter("forecast_days", FORECAST_DAYS)
        }.body()

    private companion object {
        const val GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search"
        const val FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
        const val FORECAST_DAYS = 5
    }
}

private const val REQUEST_TIMEOUT_MILLIS = 15_000L
