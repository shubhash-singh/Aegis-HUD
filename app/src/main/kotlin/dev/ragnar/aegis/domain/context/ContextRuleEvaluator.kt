package dev.ragnar.aegis.domain.context

import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed

/**
 * Pure evaluation engine for context-aware HUD rules.
 *
 * Evaluates triggers deterministically against a [ContextSnapshot] in priority order.
 * Operates entirely in-memory with zero background services, zero location access,
 * and zero battery overhead.
 */
object ContextRuleEvaluator {

    /**
     * Evaluates the given [rules] against the [snapshot].
     *
     * @param isContextAwareEnabled Master switch controlling whether context evaluation is active.
     * @param enabledRuleIds Set of rule IDs that the user has enabled.
     * @param rules List of candidate [ContextRule]s (typically [StandardContextRules.ALL_RULES]).
     * @param snapshot Current environmental and device hardware context.
     * @return [ContextEvaluationResult] containing active rule IDs, effective accent overrides,
     *         animation speed overrides, and active alert messages.
     */
    fun evaluate(
        isContextAwareEnabled: Boolean,
        enabledRuleIds: Set<String>,
        rules: List<ContextRule>,
        snapshot: ContextSnapshot,
    ): ContextEvaluationResult {
        if (!isContextAwareEnabled) {
            return ContextEvaluationResult()
        }

        val activeRuleIds = mutableSetOf<String>()
        var effectiveAccent: AccentColor? = null
        var effectiveSpeed: AnimationSpeed? = null
        val activeAlerts = mutableListOf<String>()

        // Sort rules by priority descending (higher priority evaluated first)
        val sortedRules = rules
            .filter { it.id in enabledRuleIds }
            .sortedByDescending { it.priority }

        for (rule in sortedRules) {
            if (matchesTrigger(rule.trigger, snapshot)) {
                activeRuleIds.add(rule.id)
                for (action in rule.actions) {
                    when (action) {
                        is ContextAction.SetAccent -> {
                            if (effectiveAccent == null) {
                                effectiveAccent = action.accent
                            }
                        }
                        is ContextAction.SetAnimationSpeed -> {
                            if (effectiveSpeed == null) {
                                effectiveSpeed = action.speed
                            }
                        }
                        is ContextAction.ShowAlert -> {
                            activeAlerts.add(action.alertText)
                        }
                    }
                }
            }
        }

        return ContextEvaluationResult(
            activeRuleIds = activeRuleIds,
            effectiveAccent = effectiveAccent,
            effectiveAnimationSpeed = effectiveSpeed,
            activeAlerts = activeAlerts,
        )
    }

    private fun matchesTrigger(trigger: ContextTrigger, snapshot: ContextSnapshot): Boolean {
        return when (trigger) {
            is ContextTrigger.TimeWindow -> {
                val start = trigger.startHour
                val end = trigger.endHour
                if (start == end) {
                    true
                } else if (start < end) {
                    snapshot.hourOfDay in start until end
                } else {
                    // Midnight wrap-around (e.g. 22..6: 22, 23, 0, 1, 2, 3, 4, 5)
                    snapshot.hourOfDay >= start || snapshot.hourOfDay < end
                }
            }
            is ContextTrigger.BatteryLevelBelow -> {
                snapshot.batteryPercent in 0..trigger.thresholdPercent
            }
            is ContextTrigger.PowerSaveActive -> {
                snapshot.isPowerSaveMode
            }
            is ContextTrigger.NetworkDisconnected -> {
                !snapshot.isNetworkConnected
            }
        }
    }
}
