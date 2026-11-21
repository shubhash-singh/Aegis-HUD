package dev.ragnar.aegis.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.ragnar.aegis.ui.apps.AppDrawer
import dev.ragnar.aegis.ui.apps.AppDrawerViewModel
import dev.ragnar.aegis.ui.apps.AppDrawerViewModelFactory
import dev.ragnar.aegis.ui.components.HudCornerBrackets
import dev.ragnar.aegis.ui.components.ScanLineOverlay
import dev.ragnar.aegis.ui.drawer.AppDrawerGestureIndicator
import dev.ragnar.aegis.ui.home.panels.AiStatus
import dev.ragnar.aegis.ui.home.panels.FloatingAiStatusPanel
import dev.ragnar.aegis.ui.home.panels.FloatingCorePanel
import dev.ragnar.aegis.ui.home.panels.FloatingDevicePanel
import dev.ragnar.aegis.ui.home.panels.FloatingIntelligencePanel
import dev.ragnar.aegis.ui.home.panels.FloatingPerformancePanel
import dev.ragnar.aegis.ui.home.panels.FloatingSystemStatusPanel
import dev.ragnar.aegis.ui.home.panels.FloatingTelemetryPanel
import dev.ragnar.aegis.ui.home.visualization.AICoreVisualization
import dev.ragnar.aegis.ui.settings.SettingsSheet
import dev.ragnar.aegis.ui.settings.SettingsViewModel
import dev.ragnar.aegis.ui.settings.SettingsViewModelFactory
import dev.ragnar.aegis.ui.telemetry.TelemetryViewModel
import dev.ragnar.aegis.ui.telemetry.TelemetryViewModelFactory
import dev.ragnar.aegis.ui.theme.AegisColorScheme
import dev.ragnar.aegis.ui.theme.AegisTheme

/**
 * AEGIS Full-Screen AI Command Interface Home Screen.
 *
 * Implements the reference design:
 * - Dominant 3D holographic neural brain visualization extending across the entire screen
 * - Downward vertical holographic projection cone and perspective emitter platform
 * - Tactical HUD corner brackets framing the display
 * - Header with live date, online indicator, and prominent centered digital clock
 * - Top floating panels: AEGIS AI CORE and AI STATUS
 * - Mid floating panels: CPU, RAM, TEMP, BATT with 8-segment progress bars
 * - Lower floating panels: INTELLIGENCE and TELEMETRY
 * - Bottom floating panel: SYSTEM STATUS (network bandwidth + storage)
 * - Bottom gesture indicator: double chevron SWIPE UP TO OPEN APP DRAWER
 */
@Composable
fun HomeScreen(
    clockViewModel: ClockViewModel = viewModel(),
) {
    val context = LocalContext.current

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = remember(context.applicationContext) {
            SettingsViewModelFactory(context.applicationContext)
        },
    )
    val preferences by settingsViewModel.uiState.collectAsStateWithLifecycle()

    var isDrawerVisible by rememberSaveable { mutableStateOf(false) }
    var isSettingsVisible by rememberSaveable { mutableStateOf(false) }
    var launchError by remember { mutableStateOf<String?>(null) }

    // Launcher back press handling: closes overlays first, no-op on root home
    BackHandler {
        if (isSettingsVisible) {
            isSettingsVisible = false
        } else if (isDrawerVisible) {
            isDrawerVisible = false
        }
    }

    LaunchedEffect(preferences.is24HourClock) {
        clockViewModel.set24Hour(preferences.is24HourClock)
    }

    val clockState by clockViewModel.clockState.collectAsState()

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

    // Determine current AI operational status based on real state
    val aiStatus = when {
        appDrawerState.isLoading -> AiStatus.PROCESSING
        isDrawerVisible || isSettingsVisible -> AiStatus.ACTIVE
        else -> AiStatus.IDLE
    }

    val colors = AegisColorScheme.default().withAccent(preferences.accentColor)

    AegisTheme(colors = colors) {
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

        val isAnimationPaused = isPowerSaveMode || !preferences.animationSpeed.isEnabled
        val animationSpeedMultiplier = if (preferences.animationSpeed.isEnabled) {
            when (preferences.animationSpeed.durationMillis) {
                2000 -> 2.0f
                8000 -> 0.5f
                else -> 1.0f
            }
        } else {
            0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        // Upward swipe opens the application drawer
                        if (dragAmount < -25f && !isDrawerVisible && !isSettingsVisible) {
                            isDrawerVisible = true
                        }
                    }
                }
                .semantics { contentDescription = "AEGIS AI Command Interface" },
        ) {
            // =================================================================
            // LAYER 0: FULL-SCREEN HOLOGRAPHIC NEURAL AI CORE VISUALIZATION
            // =================================================================
            if (preferences.showRadar) {
                AICoreVisualization(
                    modifier = Modifier.fillMaxSize(),
                    isPaused = isAnimationPaused,
                    speedMultiplier = animationSpeedMultiplier,
                    colors = colors,
                )
            }

            // =================================================================
            // LAYER 1: TACTICAL HUD CORNER BRACKETS
            // =================================================================
            HudCornerBrackets(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(8.dp),
            )

            // =================================================================
            // LAYER 2: FLOATING HOLOGRAPHIC INFORMATION PANELS & CONTROLS
            // =================================================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // --- TOP SECTION: Header & Top Floating Panels ---
                Column {
                    TopBar(
                        clockState = clockState,
                        showSeconds = preferences.showClockSeconds,
                        onSettingsClick = { isSettingsVisible = true },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        FloatingCorePanel()
                        FloatingAiStatusPanel(status = aiStatus)
                    }
                }

                // --- MIDDLE SECTION: Performance (CPU/RAM) & Device Status (TEMP/BATT) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FloatingPerformancePanel(
                        cpuPercent = telemetryState.systemPerformance.cpuUtilizationPercent,
                        ramPercent = telemetryState.systemPerformance.ramPercent,
                    )

                    FloatingDevicePanel(
                        batteryTemperatureLabel = telemetryState.batteryTemperatureLabel,
                        batteryPercentage = telemetryState.battery.percentage,
                        chargingStatus = telemetryState.battery.chargingStatus,
                    )
                }

                // --- LOWER SECTION: Intelligence & Telemetry Panels ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FloatingIntelligencePanel(
                        contextAvailable = telemetryState.intelligence.contextAvailable,
                        voiceAvailable = telemetryState.intelligence.voiceAvailable,
                        visionAvailable = telemetryState.intelligence.visionAvailable,
                        automationAvailable = telemetryState.intelligence.automationAvailable,
                    )

                    if (preferences.showTelemetry) {
                        FloatingTelemetryPanel(
                            batteryTemperatureLabel = telemetryState.batteryTemperatureLabel,
                            uptimeFormatted = telemetryState.uptimeFormatted,
                            batteryVoltageMv = telemetryState.battery.voltageMv,
                        )
                    }
                }

                // --- BOTTOM SECTION: System Status Panel & Swipe-Up Indicator ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (preferences.showSystemStatus) {
                        FloatingSystemStatusPanel(
                            networkLabel = telemetryState.networkLabel,
                            networkStatusLevel = telemetryState.networkStatusLevel,
                            storageStatusLabel = telemetryState.storageLabel,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Gesture indicator at bottom
                    AppDrawerGestureIndicator(
                        onClick = { isDrawerVisible = true },
                    )
                }
            }

            // CRT Scanline Overlay
            ScanLineOverlay(isPaused = isPowerSaveMode)
        }

        // =====================================================================
        // APPLICATION DRAWER (Modal Bottom Sheet)
        // =====================================================================
        if (isDrawerVisible) {
            AppDrawer(
                state = appDrawerState.copy(errorMessage = launchError ?: appDrawerState.errorMessage),
                onDismiss = {
                    isDrawerVisible = false
                    appDrawerViewModel.clearSearch()
                },
                onAppClick = { app ->
                    launchError = appDrawerViewModel.launch(app)
                    if (launchError == null) {
                        isDrawerVisible = false
                        appDrawerViewModel.clearSearch()
                    }
                },
                onToggleFavorite = appDrawerViewModel::toggleFavorite,
                onCycleSortOrder = appDrawerViewModel::cycleSortOrder,
                onCycleGridColumns = appDrawerViewModel::cycleGridColumns,
                onSearchQueryChange = appDrawerViewModel::onSearchQueryChanged,
                onClearSearch = appDrawerViewModel::clearSearch,
                onSearchAction = {
                    val error = appDrawerViewModel.launchTopResult()
                    if (error != null) {
                        launchError = error
                    } else if (appDrawerState.filteredApps.isNotEmpty()) {
                        isDrawerVisible = false
                        appDrawerViewModel.clearSearch()
                    }
                },
                initialSearchFocus = false,
            )
        }

        // =====================================================================
        // CUSTOMIZATION SETTINGS MODAL
        // =====================================================================
        if (isSettingsVisible) {
            SettingsSheet(
                preferences = preferences,
                onAccentColorChange = settingsViewModel::setAccentColor,
                onAnimationSpeedChange = settingsViewModel::setAnimationSpeed,
                onToggleRadar = settingsViewModel::setShowRadar,
                onToggleSystemStatus = settingsViewModel::setShowSystemStatus,
                onToggleTelemetry = settingsViewModel::setShowTelemetry,
                onToggleQuickActions = settingsViewModel::setShowQuickActions,
                onToggle24HourClock = settingsViewModel::set24HourClock,
                onToggleClockSeconds = settingsViewModel::setShowClockSeconds,
                onAppSortOrderChange = settingsViewModel::setAppSortOrder,
                onGridColumnsChange = settingsViewModel::setDrawerGridColumns,
                onClearFavorites = settingsViewModel::clearFavorites,
                onResetDefaults = settingsViewModel::resetToDefaults,
                onDismiss = { isSettingsVisible = false },
            )
        }
    }
}
