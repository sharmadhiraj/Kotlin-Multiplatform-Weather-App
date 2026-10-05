package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.math.roundToInt

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F");

    fun format(celsius: Float): String {
        val value = if (this == FAHRENHEIT) celsius * 9 / 5 + 32 else celsius
        return "${value.roundToInt()}$symbol"
    }
}

data class DailyUi(
    val day: String,
    val condition: String,
    val high: String,
    val low: String,
)

data class WeatherUi(
    val locationName: String,
    val temperature: String,
    val condition: String,
    val humidity: String,
    val wind: String,
    val updated: String,
    val daily: List<DailyUi>,
)

fun Weather.toUi(location: Location, unit: TemperatureUnit): WeatherUi = WeatherUi(
    locationName = listOfNotNull(location.name, location.country).joinToString(", "),
    temperature = unit.format(current.temperature),
    condition = describeWeatherCode(current.weatherCode),
    humidity = "${current.humidity}%",
    wind = "${current.windSpeed.roundToInt()} km/h",
    updated = formatDateTime(current.time),
    daily = dailyForecast(unit),
)

private fun Weather.dailyForecast(unit: TemperatureUnit): List<DailyUi> =
    daily.dates.indices.mapNotNull { index ->
        val high = daily.maxTemperatures.getOrNull(index) ?: return@mapNotNull null
        val low = daily.minTemperatures.getOrNull(index) ?: return@mapNotNull null
        val code = daily.weatherCodes.getOrNull(index) ?: return@mapNotNull null
        DailyUi(
            day = formatDay(daily.dates[index]),
            condition = describeWeatherCode(code),
            high = unit.format(high),
            low = unit.format(low),
        )
    }

fun formatDateTime(isoDateTime: String): String = try {
    val dateTime = LocalDateTime.parse(isoDateTime)
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    "${dateTime.day} ${dateTime.month.name.capitalised().take(3)}, $hour:$minute"
} catch (e: IllegalArgumentException) {
    isoDateTime
}

fun formatDay(isoDate: String): String = try {
    val date = LocalDate.parse(isoDate)
    "${date.dayOfWeek.name.capitalised().take(3)} ${date.day}"
} catch (e: IllegalArgumentException) {
    isoDate
}

private fun String.capitalised(): String = lowercase().replaceFirstChar { it.uppercase() }

fun describeWeatherCode(code: Int): String = when (code) {
    0 -> "Clear sky"
    1 -> "Mainly clear"
    2 -> "Partly cloudy"
    3 -> "Overcast"
    45, 48 -> "Fog"
    51, 53, 55 -> "Drizzle"
    56, 57 -> "Freezing drizzle"
    61, 63, 65 -> "Rain"
    66, 67 -> "Freezing rain"
    71, 73, 75, 77 -> "Snow"
    80, 81, 82 -> "Rain showers"
    85, 86 -> "Snow showers"
    95 -> "Thunderstorm"
    96, 99 -> "Thunderstorm with hail"
    else -> "Unknown"
}
