package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds


class Greeting {
    private val platform: Platform = getPlatform()
    private val api = Api()

    fun greet(): Flow<String> = flow {
        emit(if (Random.nextBoolean()) "Hi!" else "Hello!")
        delay(1.seconds)
        emit("From ${platform.name}")
        delay(1.seconds)
        emit(daysPhrase())
        emit(api.getWeatherMessage())
    }
}