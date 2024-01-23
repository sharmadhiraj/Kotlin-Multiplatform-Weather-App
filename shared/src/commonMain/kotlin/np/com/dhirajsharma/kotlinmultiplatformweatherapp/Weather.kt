package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    @SerialName("current")
    val current: Current
)

@Serializable
data class Current(
    @SerialName("time")
    val time: String,
    @SerialName("temperature_2m")
    val temperature: Float,
)