package np.com.dhirajsharma.kotlinmultiplatformweatherapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform