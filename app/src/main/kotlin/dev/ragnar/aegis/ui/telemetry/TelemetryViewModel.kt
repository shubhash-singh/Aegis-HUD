package dev.ragnar.aegis.ui.telemetry

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ragnar.aegis.data.telemetry.AndroidBatteryRepository
import dev.ragnar.aegis.data.telemetry.AndroidNetworkRepository
import dev.ragnar.aegis.data.telemetry.BatteryRepository
import dev.ragnar.aegis.data.telemetry.NetworkRepository
import dev.ragnar.aegis.domain.telemetry.BatteryState
import dev.ragnar.aegis.domain.telemetry.ChargingStatus
import dev.ragnar.aegis.domain.telemetry.NetworkState
import dev.ragnar.aegis.domain.telemetry.NetworkType
import dev.ragnar.aegis.ui.components.StatusLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

/**
 * UI-ready telemetry state for the HUD dashboard.
 */
data class TelemetryUiState(
    val battery: BatteryState = BatteryState(),
    val network: NetworkState = NetworkState(),
    val uptimeFormatted: String = "---",
    val batteryLabel: String = "---",
    val batteryStatusLevel: StatusLevel = StatusLevel.Unknown,
    val batteryTemperatureLabel: String = "---",
    val networkLabel: String = "---",
    val networkStatusLevel: StatusLevel = StatusLevel.Unknown,
)

/**
 * ViewModel managing live telemetry subscriptions for the HUD.
 */
class TelemetryViewModel(
    batteryRepository: BatteryRepository,
    networkRepository: NetworkRepository,
) : ViewModel() {

    // Ticks uptime every 30 seconds
    private val uptimeFlow: Flow<Long> = flow {
        while (true) {
            emit(SystemClock.elapsedRealtime())
            delay(30_000)
        }
    }

    val uiState: StateFlow<TelemetryUiState> = combine(
        batteryRepository.observeBatteryState(),
        networkRepository.observeNetworkState(),
        uptimeFlow,
    ) { battery, network, uptimeMillis ->
        TelemetryUiState(
            battery = battery,
            network = network,
            uptimeFormatted = formatUptime(uptimeMillis),
            batteryLabel = formatBatteryLabel(battery),
            batteryStatusLevel = determineBatteryStatusLevel(battery),
            batteryTemperatureLabel = formatTemperature(battery.temperatureCelsius),
            networkLabel = formatNetworkLabel(network),
            networkStatusLevel = determineNetworkStatusLevel(network),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TelemetryUiState(),
    )

    companion object {
        fun formatUptime(uptimeMillis: Long): String {
            val totalSeconds = uptimeMillis / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            return "${hours}H ${minutes}M"
        }

        fun formatBatteryLabel(battery: BatteryState): String {
            val pct = battery.percentage ?: return "---"
            val stateCode = when (battery.chargingStatus) {
                ChargingStatus.Charging -> "CHG"
                ChargingStatus.Full -> "FULL"
                ChargingStatus.Discharging -> "DIS"
                ChargingStatus.NotCharging -> "IDLE"
                ChargingStatus.Unknown -> ""
            }
            return if (stateCode.isNotEmpty()) "$pct% $stateCode" else "$pct%"
        }

        fun determineBatteryStatusLevel(battery: BatteryState): StatusLevel {
            val pct = battery.percentage ?: return StatusLevel.Unknown
            return when {
                pct <= 10 -> StatusLevel.Critical
                pct <= 20 -> StatusLevel.Warning
                else -> StatusLevel.Ok
            }
        }

        fun formatTemperature(tempCelsius: Float?): String {
            return if (tempCelsius != null && tempCelsius > 0f) {
                "%.1f°C".format(tempCelsius)
            } else {
                "---"
            }
        }

        fun formatNetworkLabel(network: NetworkState): String {
            if (!network.isConnected) return "OFFLINE"
            val typeStr = when (network.type) {
                NetworkType.Wifi -> "WIFI"
                NetworkType.Cellular -> "CELLULAR"
                NetworkType.Ethernet -> "ETHERNET"
                NetworkType.None -> "OFFLINE"
                NetworkType.Unknown -> "CONNECTED"
            }
            val downStr = dev.ragnar.aegis.data.telemetry.NetworkNormalizer.formatSpeed(network.downBytesPerSecond)
            val upStr = dev.ragnar.aegis.data.telemetry.NetworkNormalizer.formatSpeed(network.upBytesPerSecond)
            return "$typeStr - $downStr↓ $upStr↑"
        }

        fun determineNetworkStatusLevel(network: NetworkState): StatusLevel {
            return if (network.isConnected) StatusLevel.Ok else StatusLevel.Offline
        }
    }
}

/** Manual factory for TelemetryViewModel. */
class TelemetryViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        check(modelClass.isAssignableFrom(TelemetryViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return TelemetryViewModel(
            batteryRepository = AndroidBatteryRepository(appContext),
            networkRepository = AndroidNetworkRepository(appContext),
        ) as T
    }
}
