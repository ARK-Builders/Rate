package dev.arkbuilders.rate.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import dev.arkbuilders.rate.core.presentation.theme.ARKRateTheme
import dev.arkbuilders.rate.feature.quickwidget.presentation.QuickCalculationsWidgetReceiver

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            ARKRateTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }

    override fun onStop() {
        sendBroadcast(
            Intent(this, QuickCalculationsWidgetReceiver::class.java).apply {
                action = QuickCalculationsWidgetReceiver.PINNED_CALCULATIONS_REFRESH
            },
        )
        super.onStop()
    }
}
