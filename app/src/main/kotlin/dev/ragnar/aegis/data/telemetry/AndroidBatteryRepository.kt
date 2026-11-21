package dev.ragnar.aegis.data.telemetry

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import dev.ragnar.aegis.domain.telemetry.BatteryState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementation of [BatteryRepository] using Android's sticky `ACTION_BATTERY_CHANGED` broadcast.
 *
 * No polling or background service is used (NFR-002).
 */
class AndroidBatteryRepository(
    private val context: Context,
) : BatteryRepository {

    override fun observeBatteryState(): Flow<BatteryState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent != null && intent.action == Intent.ACTION_BATTERY_CHANGED) {
                    trySend(parseBatteryIntent(intent))
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = context.registerReceiver(receiver, filter)

        // Sticky broadcast delivers current state immediately
        if (stickyIntent != null) {
            trySend(parseBatteryIntent(stickyIntent))
        }

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    private fun parseBatteryIntent(intent: Intent): BatteryState {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1).takeIf { it >= 0 }
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1).takeIf { it > 0 }
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1).takeIf { it >= 0 }
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1).takeIf { it >= 0 }
        val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).takeIf { it > 0 }
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).takeIf { it > 0 }
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1).takeIf { it >= 0 }

        return BatteryNormalizer.normalize(
            level = level,
            scale = scale,
            status = status,
            plugged = plugged,
            temperatureTenths = temp,
            voltageMv = voltage,
            health = health,
        )
    }
}
