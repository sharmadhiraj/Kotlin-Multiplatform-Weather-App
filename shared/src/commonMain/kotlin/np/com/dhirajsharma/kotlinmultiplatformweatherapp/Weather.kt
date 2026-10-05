package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    @SerialName("current")
    val current: Current,
    @SerialName("daily")
    val daily: Daily,
)

@Serializable
data class Current(
    @SerialName("time")
    val time: String,
    @SerialName("temperature_2m")
    val temperature: Float,
    @SerialName("relative_humidity_2m")
    val humidity: Int,
    @SerialName("wind_speed_10m")
    val windSpeed: Float,
    @SerialName("weather_code")
    val weatherCode: Int,
)

@Serializable
data class Daily(
    @SerialName("time")
    val dates: List<String>,
    @SerialName("temperature_2m_max")
    val maxTemperatures: List<Float>,
    @SerialName("temperature_2m_min")
    val minTemperatures: List<Float>,
    @SerialName("weather_code")
    val weatherCodes: List<Int>,
)

@Serializable
data class Location(
    @SerialName("name")
    val name: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("country")
    val country: String? = null,
)

@Serializable
data class GeocodingResponse(
    @SerialName("results")
    val results: List<Location> = emptyList(),
)
