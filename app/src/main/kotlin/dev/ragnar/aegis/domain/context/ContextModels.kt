package dev.ragnar.aegis.domain.context

import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed

/**
 * Trigger conditions for context-aware HUD rules.
 */
sealed interface ContextTrigger {
    /**
     * Matches if current [hourOfDay] (0..23) is within [startHour, endHour).
     * Correctly handles midnight wrap-around (e.g., startHour = 22, endHour = 6).
     */
    data class TimeWindow(val startHour: Int, val endHour: Int) : ContextTrigger

    /**
     * Matches if current battery percentage is less than or equal to [thresholdPercent].
     */
    data class BatteryLevelBelow(val thresholdPercent: Int) : ContextTrigger

    /**
     * Matches if OS Power Save mode is currently active.
     */
    data object PowerSaveActive : ContextTrigger

    /**
     * Matches if network connectivity is offline (neither Wi-Fi nor Cellular).
     */
    data object NetworkDisconnected : ContextTrigger
}

/**
 * Action taken by the HUD when a context rule fires.
 */
sealed interface ContextAction {
    data class SetAccent(val accent: AccentColor) : ContextAction
    data class SetAnimationSpeed(val speed: AnimationSpeed) : ContextAction
    data class ShowAlert(val alertText: String) : ContextAction
}

/**
 * Immutable definition of a context-aware HUD rule.
 */
data class ContextRule(
    val id: String,
    val name: String,
    val description: String,
    val trigger: ContextTrigger,
    val actions: List<ContextAction>,
    val priority: Int,
)

/**
 * Current environmental and device hardware context snapshot.
 */
data class ContextSnapshot(
    val hourOfDay: Int,
    val minute: Int,
    val batteryPercent: Int,
    val isPowerSaveMode: Boolean,
    val isNetworkConnected: Boolean,
)

/**
 * Result of evaluating context-aware rules against a [ContextSnapshot].
 */
data class ContextEvaluationResult(
    val activeRuleIds: Set<String> = emptySet(),
    val effectiveAccent: AccentColor? = null,
    val effectiveAnimationSpeed: AnimationSpeed? = null,
    val activeAlerts: List<String> = emptyList(),
)

/**
 * Predefined catalog of standard AEGIS context-aware protocols.
 */
object StandardContextRules {
    val LOW_BATTERY = ContextRule(
        id = "rule_low_battery",
        name = "LOW BATTERY DEFENSE",
        description = "Battery ≤ 15%: Shift HUD to Crimson, disable animations, and display alert.",
        trigger = ContextTrigger.BatteryLevelBelow(thresholdPercent = 15),
        actions = listOf(
            ContextAction.SetAccent(AccentColor.Crimson),
            ContextAction.SetAnimationSpeed(AnimationSpeed.Disabled),
            ContextAction.ShowAlert("CRITICAL BATTERY: CRIMSON DEFENSE ENGAGED"),
        ),
        priority = 100,
    )

    val POWER_SAVE = ContextRule(
        id = "rule_power_save",
        name = "POWER SAVE MODE",
        description = "OS Power Saver active: Set animations to slow and display status banner.",
        trigger = ContextTrigger.PowerSaveActive,
        actions = listOf(
            ContextAction.SetAnimationSpeed(AnimationSpeed.Slow),
            ContextAction.ShowAlert("SYSTEM IN POWER SAVE MODE"),
        ),
        priority = 80,
    )

    val NETWORK_OFFLINE = ContextRule(
        id = "rule_network_offline",
        name = "COMM LINK LOSS",
        description = "Network offline: Display tactical telemetry loss warning banner.",
        trigger = ContextTrigger.NetworkDisconnected,
        actions = listOf(
            ContextAction.ShowAlert("COMM LINK OFFLINE — LOCAL CACHE MODE"),
        ),
        priority = 50,
    )

    val NIGHT_OPS = ContextRule(
        id = "rule_night_ops",
        name = "NIGHT OPS PROTOCOL",
        description = "22:00 - 06:00: Shift HUD accent to Alert Amber night vision mode.",
        trigger = ContextTrigger.TimeWindow(startHour = 22, endHour = 6),
        actions = listOf(
            ContextAction.SetAccent(AccentColor.Amber),
        ),
        priority = 40,
    )

    val MORNING_PROTOCOL = ContextRule(
        id = "rule_morning_brief",
        name = "MORNING PROTOCOL",
        description = "06:00 - 12:00: Shift HUD accent to Matrix Green diagnostic theme.",
        trigger = ContextTrigger.TimeWindow(startHour = 6, endHour = 12),
        actions = listOf(
            ContextAction.SetAccent(AccentColor.Green),
        ),
        priority = 30,
    )

    val ALL_RULES: List<ContextRule> = listOf(
        LOW_BATTERY,
        POWER_SAVE,
        NETWORK_OFFLINE,
        NIGHT_OPS,
        MORNING_PROTOCOL,
    )

    val DEFAULT_ENABLED_IDS: Set<String> = setOf(
        LOW_BATTERY.id,
        POWER_SAVE.id,
        NETWORK_OFFLINE.id,
        NIGHT_OPS.id,
    )
}
