package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherStoreTest {

    private fun TestScope.storeScope() = CoroutineScope(UnconfinedTestDispatcher(testScheduler))

    private val berlin = Location("Berlin", 52.5, 13.4, "Germany")
    private val weather = Weather(
        current = Current("2026-10-05T10:00", 20f, 55, 10f, 0),
        daily = Daily(listOf("2026-10-05"), listOf(22f), listOf(11f), listOf(0)),
    )

    private class FakeRepository(
        var location: Location? = null,
        var weather: Weather? = null,
    ) : WeatherRepository {
        override suspend fun findLocation(name: String): Location =
            location ?: throw LocationNotFoundException(name)

        override suspend fun getWeather(location: Location): Weather =
            weather ?: error("boom")
    }

    @Test
    fun loadsDefaultCityOnStart() = runTest {
        val store = WeatherStore(FakeRepository(berlin, weather), storeScope())
        advanceUntilIdle()
        val state = store.state.value
        assertFalse(state.isLoading)
        assertEquals("20°C", state.weather?.temperature)
        assertEquals("Berlin, Germany", state.weather?.locationName)
    }

    @Test
    fun reportsMissingLocation() = runTest {
        val store = WeatherStore(FakeRepository(), storeScope())
        advanceUntilIdle()
        assertEquals("No location found for \"Berlin\"", store.state.value.error)
        assertNull(store.state.value.weather)
    }

    @Test
    fun reportsGenericFailure() = runTest {
        val store = WeatherStore(FakeRepository(berlin, null), storeScope())
        advanceUntilIdle()
        assertNotNull(store.state.value.error)
    }

    @Test
    fun togglesUnit() = runTest {
        val store = WeatherStore(FakeRepository(berlin, weather), storeScope())
        advanceUntilIdle()
        store.toggleUnit()
        assertEquals("68°F", store.state.value.weather?.temperature)
        store.toggleUnit()
        assertEquals("20°C", store.state.value.weather?.temperature)
    }
}
