package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherFormatterTest {

    @Test
    fun formatsCelsiusAndFahrenheit() {
        assertEquals("21°C", TemperatureUnit.CELSIUS.format(21.4f))
        assertEquals("71°F", TemperatureUnit.FAHRENHEIT.format(21.4f))
    }

    @Test
    fun formatsDateTime() {
        assertEquals("5 Oct, 09:05", formatDateTime("2026-10-05T09:05"))
    }

    @Test
    fun formatsDay() {
        assertEquals("Mon 5", formatDay("2026-10-05"))
    }

    @Test
    fun fallsBackToRawValueWhenUnparsable() {
        assertEquals("garbage", formatDateTime("garbage"))
    }

    @Test
    fun describesUnknownCode() {
        assertEquals("Unknown", describeWeatherCode(1234))
    }
}
