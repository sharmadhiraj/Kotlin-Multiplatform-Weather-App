package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun daysUntilNewYear(): Int {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val closestNewYear = LocalDate(today.year + 1, 1, 1)
    return today.date.daysUntil(closestNewYear)
}

fun daysPhrase(): String = "There are only ${daysUntilNewYear()} days left until New Year! 🎆"