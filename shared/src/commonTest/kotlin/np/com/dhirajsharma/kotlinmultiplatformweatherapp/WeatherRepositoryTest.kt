package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WeatherRepositoryTest {

    private fun repository(body: String, status: HttpStatusCode = HttpStatusCode.OK) =
        KtorWeatherRepository(
            HttpClient(
                MockEngine {
                    respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
                },
            ) {
                expectSuccess = true
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            },
        )

    @Test
    fun findsLocation() = runTest {
        val json = """{"results":[{"name":"Berlin","latitude":52.5,"longitude":13.4,"country":"Germany"}]}"""
        assertEquals("Berlin", repository(json).findLocation("berlin").name)
    }

    @Test
    fun throwsWhenLocationMissing() = runTest {
        assertFailsWith<LocationNotFoundException> { repository("{}").findLocation("nowhere") }
    }

    @Test
    fun parsesWeather() = runTest {
        val json = """
            {"current":{"time":"2026-10-05T10:00","temperature_2m":12.5,"relative_humidity_2m":60,
             "wind_speed_10m":9.0,"weather_code":3,"unknown":1},
             "daily":{"time":["2026-10-05"],"temperature_2m_max":[14.0],"temperature_2m_min":[8.0],"weather_code":[61]}}
        """.trimIndent()
        val weather = repository(json).getWeather(Location("Berlin", 52.5, 13.4))
        assertEquals(12.5f, weather.current.temperature)
        assertEquals(listOf(61), weather.daily.weatherCodes)
    }

    @Test
    fun failsOnHttpError() = runTest {
        assertFailsWith<ClientRequestException> {
            repository("{}", HttpStatusCode.BadRequest).getWeather(Location("Berlin", 52.5, 13.4))
        }
    }
}
