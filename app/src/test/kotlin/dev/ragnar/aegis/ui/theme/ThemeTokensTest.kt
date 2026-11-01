package dev.ragnar.aegis.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for the AEGIS design system tokens.
 *
 * Verifies that default factory methods produce valid, non-null token sets
 * and that key values match the UI/UX specification.
 */
class ThemeTokensTest {

    @Test
    fun `default color scheme has all tokens`() {
        val colors = AegisColorScheme.default()
        // Base colors
        assertNotNull(colors.background)
        assertNotNull(colors.surface)
        assertNotNull(colors.surfaceElevated)
        assertNotNull(colors.border)
        assertNotNull(colors.borderActive)
        assertNotNull(colors.divider)
        // Accent
        assertNotNull(colors.cyan)
        assertNotNull(colors.cyanDim)
        assertNotNull(colors.amber)
        assertNotNull(colors.amberDim)
        // Status
        assertNotNull(colors.statusOk)
        assertNotNull(colors.statusWarning)
        assertNotNull(colors.statusCritical)
        assertNotNull(colors.statusOffline)
        assertNotNull(colors.statusUnknown)
        // Text
        assertNotNull(colors.textPrimary)
        assertNotNull(colors.textSecondary)
        assertNotNull(colors.textAccent)
        assertNotNull(colors.textWarning)
        assertNotNull(colors.textMuted)
    }

    @Test
    fun `default typography scheme has all 8 styles`() {
        val typography = AegisTypographyScheme.default()
        assertNotNull(typography.clock)
        assertNotNull(typography.clockSmall)
        assertNotNull(typography.telemetry)
        assertNotNull(typography.telemetryLabel)
        assertNotNull(typography.appName)
        assertNotNull(typography.body)
        assertNotNull(typography.label)
        assertNotNull(typography.sectionHeader)
    }

    @Test
    fun `default spacing has all tokens`() {
        val spacing = AegisSpacing.default()
        assertNotNull(spacing.xs)
        assertNotNull(spacing.sm)
        assertNotNull(spacing.md)
        assertNotNull(spacing.lg)
        assertNotNull(spacing.xl)
        assertNotNull(spacing.xxl)
        assertNotNull(spacing.panelBorderWidth)
        assertNotNull(spacing.panelCornerRadius)
        assertNotNull(spacing.panelTickLength)
        assertNotNull(spacing.statusDotSize)
        assertNotNull(spacing.minTouchTarget)
        assertNotNull(spacing.appIconSize)
    }

    @Test
    fun `color scheme is an immutable data class and supports copy`() {
        val original = AegisColorScheme.default()
        val modified = original.copy(background = original.cyan)
        // copy must produce a different instance with the changed field
        assertEquals(original.cyan, modified.background)
        // original must be unchanged
        assertEquals(AegisColorScheme.default().background, original.background)
    }

    @Test
    fun `spacing values match UI spec section 4`() {
        val spacing = AegisSpacing.default()
        // Values from UI_UX_SPECIFICATION.md Section 4
        assertEquals(4f, spacing.xs.value, 0.01f)
        assertEquals(8f, spacing.sm.value, 0.01f)
        assertEquals(16f, spacing.md.value, 0.01f)
        assertEquals(24f, spacing.lg.value, 0.01f)
        assertEquals(32f, spacing.xl.value, 0.01f)
        assertEquals(48f, spacing.xxl.value, 0.01f)
        assertEquals(1f, spacing.panelBorderWidth.value, 0.01f)
        assertEquals(2f, spacing.panelCornerRadius.value, 0.01f)
        assertEquals(6f, spacing.panelTickLength.value, 0.01f)
    }
}
