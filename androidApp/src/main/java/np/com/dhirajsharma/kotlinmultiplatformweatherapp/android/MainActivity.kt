package np.com.dhirajsharma.kotlinmultiplatformweatherapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import np.com.dhirajsharma.kotlinmultiplatformweatherapp.App

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App(mainViewModel.store) }
    }
}
