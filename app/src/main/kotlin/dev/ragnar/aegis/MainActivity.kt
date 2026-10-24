package dev.ragnar.aegis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.ragnar.aegis.ui.home.HomeScreen
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * Main launcher activity for AEGIS HUD.
 *
 * Registered as an Android home-screen launcher via manifest intent-filter.
 * Uses singleTask launch mode to prevent stacked instances (DEC-010).
 * Uses stateNotNeeded=true because the launcher rebuilds from live data (DEC-010).
 * Uses excludeFromRecents=true to stay out of the Recents screen (DEC-010).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AegisTheme {
                HomeScreen()
            }
        }
    }
}
