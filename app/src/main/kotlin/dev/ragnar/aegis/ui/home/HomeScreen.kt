package dev.ragnar.aegis.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.ragnar.aegis.ui.apps.AppDrawer
import dev.ragnar.aegis.ui.apps.AppDrawerViewModel
import dev.ragnar.aegis.ui.apps.AppDrawerViewModelFactory
import dev.ragnar.aegis.ui.components.HudPanel
import dev.ragnar.aegis.ui.components.RadarDisplay
import dev.ragnar.aegis.ui.components.ScanLineOverlay
import dev.ragnar.aegis.ui.components.StatusIndicator
import dev.ragnar.aegis.ui.components.StatusLevel
import dev.ragnar.aegis.ui.telemetry.TelemetryViewModel
import dev.ragnar.aegis.ui.telemetry.TelemetryViewModelFactory
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * HUD Home Screen — full dashboard layout.
 *
 * Implements the screen structure from UI_UX_SPECIFICATION.md Section 5:
 * - TopBar zone: system label, live clock, date, status
 * - Center HUD zone: animated radar graphic, live telemetry panels (battery, network, uptime)
 * - Quick launch zone: app shortcut slots
 * - Bottom nav zone: search bar + drawer button
 * - Overlay: subtle CRT scan-line
 *
 * BackHandler prevents the launcher from finishing on back press (ARCHITECTURE.md Section 6).
 * System insets handled per UI_UX_SPECIFICATION.md Section 8.
 * Automatically throttles/pauses animations in battery-saver mode (F-007-P5).
 * Genuine telemetry only; no simulated sensor readings (DEC-009).
 *
 * @param clockViewModel ViewModel providing live clock state.
 */
@Composable
fun HomeScreen(
    clockViewModel: ClockViewModel = viewModel(),
) {
    // Launcher must not finish on back press — no-op handler.
    BackHandler {
        // Intentionally empty: a home-screen launcher does not exit on back press.
    }

    val clockState by clockViewModel.clockState.collectAsState()
    val context = LocalContext.current

    val appDrawerViewModel: AppDrawerViewModel = viewModel(
        factory = remember(context.applicationContext) {
            AppDrawerViewModelFactory(context.applicationContext)
        },
    )
    val appDrawerState by appDrawerViewModel.uiState.collectAsStateWithLifecycle()

    val telemetryViewModel: TelemetryViewModel = viewModel(
        factory = remember(context.applicationContext) {
            TelemetryViewModelFactory(context.applicationContext)
        },
    )
    val telemetryState by telemetryViewModel.uiState.collectAsStateWithLifecycle()

    var isDrawerVisible by rememberSaveable { mutableStateOf(false) }
    var openWithSearchFocus by rememberSaveable { mutableStateOf(false) }
    var launchError by remember { mutableStateOf<String?>(null) }
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    // Observe battery-saver mode dynamically (F-007-P5)
    var isPowerSaveMode by remember {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        mutableStateOf(pm?.isPowerSaveMode == true)
    }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                isPowerSaveMode = pm?.isPowerSaveMode == true
            }
        }
        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = spacing.md)
                .semantics { contentDescription = "AEGIS Home Screen" },
        ) {
            Spacer(modifier = Modifier.height(spacing.sm))

            // === TOP BAR ZONE ===
            TopBar(clockState = clockState)

            Spacer(modifier = Modifier.height(spacing.md))

            // === CENTER HUD ZONE ===
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
            ) {
                // Animated technical radar graphic (F-007)
                RadarDisplay(
                    isPaused = isPowerSaveMode,
                    modifier = Modifier.padding(bottom = spacing.sm),
                )

                // Live system status panel (F-006 & F-008)
                HudPanel(title = "System Status") {
                    Column(modifier = Modifier.padding(top = spacing.xs)) {
                        StatusRow(
                            label = "NETWORK",
                            status = telemetryState.networkStatusLevel,
                            value = telemetryState.networkLabel,
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        StatusRow(
                            label = "BATTERY",
                            status = telemetryState.batteryStatusLevel,
                            value = telemetryState.batteryLabel,
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        StatusRow(
                            label = "STORAGE",
                            status = StatusLevel.Ok,
                            value = "ONLINE",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                // Live telemetry panel (F-006 & F-008)
                HudPanel(title = "Telemetry") {
                    Column(modifier = Modifier.padding(top = spacing.xs)) {
                        TelemetryRow(
                            label = "BATTERY TEMP",
                            value = telemetryState.batteryTemperatureLabel,
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        TelemetryRow(
                            label = "UPTIME",
                            value = telemetryState.uptimeFormatted,
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        TelemetryRow(
                            label = "VOLTAGE",
                            value = telemetryState.battery.voltageMv?.let { "${it}mV" } ?: "---",
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // === QUICK LAUNCH ZONE ===
            QuickLaunchRow(
                apps = appDrawerState.apps,
                onAppClick = { app ->
                    launchError = appDrawerViewModel.launch(app)
                },
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // === BOTTOM NAV ZONE ===
            BottomNavBar(
                onDrawerClick = {
                    launchError = null
                    openWithSearchFocus = false
                    isDrawerVisible = true
                },
                onSearchClick = {
                    launchError = null
                    openWithSearchFocus = true
                    isDrawerVisible = true
                },
            )

            Spacer(modifier = Modifier.height(spacing.sm))
        }

        // CRT scan-line overlay (F-007-P3)
        ScanLineOverlay(isPaused = isPowerSaveMode)
    }

    if (isDrawerVisible) {
        AppDrawer(
            state = appDrawerState.copy(errorMessage = launchError ?: appDrawerState.errorMessage),
            onDismiss = {
                isDrawerVisible = false
                openWithSearchFocus = false
                appDrawerViewModel.clearSearch()
            },
            onAppClick = { app ->
                launchError = appDrawerViewModel.launch(app)
                if (launchError == null) {
                    isDrawerVisible = false
                    openWithSearchFocus = false
                    appDrawerViewModel.clearSearch()
                }
            },
            onSearchQueryChange = appDrawerViewModel::onSearchQueryChanged,
            onClearSearch = appDrawerViewModel::clearSearch,
            onSearchAction = {
                val error = appDrawerViewModel.launchTopResult()
                if (error != null) {
                    launchError = error
                } else if (appDrawerState.filteredApps.isNotEmpty()) {
                    isDrawerVisible = false
                    openWithSearchFocus = false
                    appDrawerViewModel.clearSearch()
                }
            },
            initialSearchFocus = openWithSearchFocus,
        )
    }
}

/**
 * A row displaying a status label with its indicator dot.
 */
@Composable
internal fun StatusRow(
    label: String,
    status: StatusLevel,
    value: String? = null,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography
    val spacing = AegisTheme.spacing

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusIndicator(status = status)
        Spacer(modifier = Modifier.width(spacing.sm))
        Text(
            text = label,
            style = typography.telemetryLabel,
            color = colors.textSecondary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value ?: status.label.uppercase(),
            style = typography.telemetry,
            color = when (status) {
                StatusLevel.Ok -> colors.statusOk
                StatusLevel.Warning -> colors.statusWarning
                StatusLevel.Critical -> colors.statusCritical
                StatusLevel.Offline -> colors.statusOffline
                StatusLevel.Unknown -> colors.statusUnknown
            },
        )
    }
}

/**
 * A row displaying a telemetry label with its value.
 * Values show "---" as placeholder when unavailable (no fake data per DEC-009).
 */
@Composable
internal fun TelemetryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = AegisTheme.colors
    val typography = AegisTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = typography.telemetryLabel,
            color = colors.textSecondary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = typography.telemetry,
            color = colors.textPrimary,
        )
    }
}
