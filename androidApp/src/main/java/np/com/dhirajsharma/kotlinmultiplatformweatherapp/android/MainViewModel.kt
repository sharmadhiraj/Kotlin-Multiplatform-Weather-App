package np.com.dhirajsharma.kotlinmultiplatformweatherapp.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import np.com.dhirajsharma.kotlinmultiplatformweatherapp.KtorWeatherRepository
import np.com.dhirajsharma.kotlinmultiplatformweatherapp.WeatherStore

class MainViewModel : ViewModel() {
    val store = WeatherStore(KtorWeatherRepository(), viewModelScope)
}
