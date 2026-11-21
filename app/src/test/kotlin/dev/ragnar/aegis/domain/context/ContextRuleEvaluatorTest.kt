package dev.ragnar.aegis.domain.context

import dev.ragnar.aegis.domain.settings.AccentColor
import dev.ragnar.aegis.domain.settings.AnimationSpeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextRuleEvaluatorTest {

    private val allRuleIds = StandardContextRules.ALL_RULES.map { it.id }.toSet()

    private fun makeSnapshot(
        hourOfDay: Int = 10,
        minute: Int = 0,
        batteryPercent: Int = 80,
        isPowerSaveMode: Boolean = false,
        isNetworkConnected: Boolean = true,
    ) = ContextSnapshot(
        hourOfDay = hourOfDay,
        minute = minute,
        batteryPercent = batteryPercent,
        isPowerSaveMode = isPowerSaveMode,
        isNetworkConnected = isNetworkConnected,
    )

    // UT-013-01: Time-based trigger with mock time input
    @Test
    fun `time window rule activates within window`() {
        val snapshot = makeSnapshot(hourOfDay = 23) // 23:00 is within 22:00–06:00 night window
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.NIGHT_OPS.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("NIGHT_OPS should be active at 23:00", result.activeRuleIds.contains(StandardContextRules.NIGHT_OPS.id))
        assertEquals("Effective accent should be Amber at night", AccentColor.Amber, result.effectiveAccent)
    }

    @Test
    fun `time window midnight wrap-around activates at hour 2`() {
        val snapshot = makeSnapshot(hourOfDay = 2) // 02:00 is also in 22:00–06:00 window
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.NIGHT_OPS.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("NIGHT_OPS should be active at 02:00 (midnight wrap)", result.activeRuleIds.contains(StandardContextRules.NIGHT_OPS.id))
    }

    @Test
    fun `time window rule does not activate outside window`() {
        val snapshot = makeSnapshot(hourOfDay = 14) // 14:00 is outside 22:00–06:00 window
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.NIGHT_OPS.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertFalse("NIGHT_OPS should NOT be active at 14:00", result.activeRuleIds.contains(StandardContextRules.NIGHT_OPS.id))
    }

    // UT-013-02: Battery-level trigger
    @Test
    fun `low battery rule activates at critical level`() {
        val snapshot = makeSnapshot(batteryPercent = 10)
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.LOW_BATTERY.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("LOW_BATTERY should activate at 10%", result.activeRuleIds.contains(StandardContextRules.LOW_BATTERY.id))
        assertEquals("Effective accent should be Crimson on low battery", AccentColor.Crimson, result.effectiveAccent)
        assertEquals("Animations should be disabled on low battery", AnimationSpeed.Disabled, result.effectiveAnimationSpeed)
        assertTrue("Should have low battery alert", result.activeAlerts.any { it.contains("CRITICAL BATTERY") })
    }

    @Test
    fun `low battery rule does NOT activate above threshold`() {
        val snapshot = makeSnapshot(batteryPercent = 50)
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.LOW_BATTERY.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertFalse("LOW_BATTERY should NOT activate at 50%", result.activeRuleIds.contains(StandardContextRules.LOW_BATTERY.id))
    }

    // UT-013-03: No rule configured produces default HUD state
    @Test
    fun `no rule configured returns empty result`() {
        val snapshot = makeSnapshot()
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = emptySet(),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("No active rules when none are enabled", result.activeRuleIds.isEmpty())
        assertNull("No effective accent override", result.effectiveAccent)
        assertNull("No effective speed override", result.effectiveAnimationSpeed)
        assertTrue("No active alerts", result.activeAlerts.isEmpty())
    }

    @Test
    fun `master toggle disabled returns empty result regardless of snapshot`() {
        val snapshot = makeSnapshot(batteryPercent = 5, isPowerSaveMode = true, isNetworkConnected = false)
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = false,
            enabledRuleIds = allRuleIds,
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("Master toggle OFF should produce empty result", result.activeRuleIds.isEmpty())
        assertNull("No accent override when master disabled", result.effectiveAccent)
    }

    @Test
    fun `network offline rule activates when disconnected`() {
        val snapshot = makeSnapshot(isNetworkConnected = false)
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.NETWORK_OFFLINE.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("NETWORK_OFFLINE should activate", result.activeRuleIds.contains(StandardContextRules.NETWORK_OFFLINE.id))
        assertTrue("Should have COMM LINK OFFLINE alert", result.activeAlerts.any { it.contains("COMM LINK OFFLINE") })
    }

    @Test
    fun `higher priority rule accent wins over lower priority`() {
        // Both LOW_BATTERY (p=100, Crimson) and NIGHT_OPS (p=40, Amber) active
        val snapshot = makeSnapshot(hourOfDay = 23, batteryPercent = 5)
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.LOW_BATTERY.id, StandardContextRules.NIGHT_OPS.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        // LOW_BATTERY has higher priority (100) so Crimson should win
        assertEquals("Crimson should win over Amber due to priority", AccentColor.Crimson, result.effectiveAccent)
    }

    @Test
    fun `power save rule activates when power save mode is on`() {
        val snapshot = makeSnapshot(isPowerSaveMode = true)
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.POWER_SAVE.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("POWER_SAVE should activate", result.activeRuleIds.contains(StandardContextRules.POWER_SAVE.id))
        assertEquals("Speed should be Slow on power save", AnimationSpeed.Slow, result.effectiveAnimationSpeed)
    }

    // UT-013-04: Multiple alerts accumulate
    @Test
    fun `multiple active rules accumulate alerts`() {
        val snapshot = makeSnapshot(isNetworkConnected = false, isPowerSaveMode = true)
        val result = ContextRuleEvaluator.evaluate(
            isContextAwareEnabled = true,
            enabledRuleIds = setOf(StandardContextRules.NETWORK_OFFLINE.id, StandardContextRules.POWER_SAVE.id),
            rules = StandardContextRules.ALL_RULES,
            snapshot = snapshot,
        )
        assertTrue("Should have 2 active rules", result.activeRuleIds.size == 2)
        assertTrue("Should have 2 alerts", result.activeAlerts.size == 2)
    }
}
