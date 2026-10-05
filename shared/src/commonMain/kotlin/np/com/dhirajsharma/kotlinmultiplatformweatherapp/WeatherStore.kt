package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isLoading: Boolean = true,
    val weather: WeatherUi? = null,
    val error: String? = null,
)

class WeatherStore(
    private val repository: WeatherRepository,
    private val scope: CoroutineScope,
) {
    constructor() : this(KtorWeatherRepository(), CoroutineScope(SupervisorJob() + Dispatchers.Main))

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private var location: Location? = null
    private var weather: Weather? = null
    private var job: Job? = null

    init {
        search(DEFAULT_CITY)
    }

    fun search(city: String) {
        val name = city.trim()
        if (name.isEmpty()) return
        load { repository.findLocation(name) }
    }

    fun refresh() {
        val current = location
        if (current == null) search(DEFAULT_CITY) else load { current }
    }

    fun toggleUnit() {
        val newUnit = if (_state.value.unit == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }
        val currentLocation = location
        val currentWeather = weather
        _state.update {
            it.copy(
                unit = newUnit,
                weather = if (currentLocation != null && currentWeather != null) {
                    currentWeather.toUi(currentLocation, newUnit)
                } else {
                    it.weather
                },
            )
        }
    }

    private fun load(resolveLocation: suspend () -> Location) {
        job?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }
        job = scope.launch {
            try {
                val resolved = resolveLocation()
                val loaded = repository.getWeather(resolved)
                location = resolved
                weather = loaded
                _state.update {
                    it.copy(isLoading = false, weather = loaded.toUi(resolved, it.unit))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
        }
    }

    private fun Exception.toUserMessage(): String = when (this) {
        is LocationNotFoundException -> "No location found for \"$query\""
        is HttpRequestTimeoutException,
        is ConnectTimeoutException,
        is SocketTimeoutException -> "The request timed out. Please try again."
        else -> "Could not load weather. Check your connection and try again."
    }

    private companion object {
        const val DEFAULT_CITY = "Berlin"
    }
}
