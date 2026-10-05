package np.com.dhirajsharma.kotlinmultiplatformweatherapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun App(store: WeatherStore) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val state by store.state.collectAsState()
            WeatherScreen(
                state = state,
                onSearch = store::search,
                onRefresh = store::refresh,
                onToggleUnit = store::toggleUnit,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    state: WeatherUiState,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onToggleUnit: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "City search field" },
                label = { Text("City") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch(query) })
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSearch(query) },
                    modifier = Modifier.semantics { contentDescription = "Search city" }
                ) { Text("Search") }
                TextButton(
                    onClick = onRefresh,
                    modifier = Modifier.semantics { contentDescription = "Refresh weather" }
                ) { Text("Refresh") }
                TextButton(
                    onClick = onToggleUnit,
                    modifier = Modifier.semantics { contentDescription = "Toggle temperature unit" }
                ) {
                    val other = if (state.unit == TemperatureUnit.CELSIUS) {
                        TemperatureUnit.FAHRENHEIT
                    } else {
                        TemperatureUnit.CELSIUS
                    }
                    Text("Switch to ${other.symbol}")
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            state.error?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
                Button(onClick = onRefresh) { Text("Retry") }
            }
            state.weather?.let { WeatherContent(it) }
        }
    }
}

@Composable
private fun WeatherContent(weather: WeatherUi) {
    Text(weather.locationName, style = MaterialTheme.typography.headlineSmall)
    Text(weather.temperature, style = MaterialTheme.typography.displayMedium)
    Text(weather.condition, style = MaterialTheme.typography.titleMedium)
    Text("Humidity ${weather.humidity}  |  Wind ${weather.wind}")
    Text("Updated ${weather.updated}", style = MaterialTheme.typography.bodySmall)
    HorizontalDivider()
    weather.daily.forEach { day ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(day.day, modifier = Modifier.weight(1f))
            Text(day.condition, modifier = Modifier.weight(2f))
            Text("${day.high} / ${day.low}")
        }
    }
}
