package dev.ragnar.aegis.domain.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [WidgetSerializer] and [HostedWidget] model.
 */
class WidgetSerializerTest {

    @Test
    fun serialize_emptyList_returnsEmptyString() {
        val result = WidgetSerializer.serialize(emptyList())
        assertEquals("", result)
    }

    @Test
    fun deserialize_nullOrBlank_returnsEmptyList() {
        assertTrue(WidgetSerializer.deserialize(null).isEmpty())
        assertTrue(WidgetSerializer.deserialize("").isEmpty())
        assertTrue(WidgetSerializer.deserialize("   ").isEmpty())
    }

    @Test
    fun roundTrip_singleWidget_preservesAllFields() {
        val widget = HostedWidget(
            appWidgetId = 42,
            providerPackage = "com.android.deskclock",
            providerClass = "com.android.deskclock.DigitalAppWidgetProvider",
            label = "Digital Clock",
            heightDp = 180,
        )

        val serialized = WidgetSerializer.serialize(listOf(widget))
        val deserialized = WidgetSerializer.deserialize(serialized)

        assertEquals(1, deserialized.size)
        assertEquals(widget, deserialized.first())
    }

    @Test
    fun roundTrip_multipleWidgets_preservesOrderAndData() {
        val widgets = listOf(
            HostedWidget(1, "com.example.weather", "com.example.weather.Widget", "Weather & Radar", 140),
            HostedWidget(2, "com.spotify.music", "com.spotify.music.Widget", "Now Playing", 200),
            HostedWidget(3, "com.google.notes", "com.google.notes.Widget", "Quick Notes", 280),
        )

        val serialized = WidgetSerializer.serialize(widgets)
        val deserialized = WidgetSerializer.deserialize(serialized)

        assertEquals(3, deserialized.size)
        assertEquals(widgets, deserialized)
    }

    @Test
    fun roundTrip_specialCharactersAndPunctuationInLabel_handledSafely() {
        val widget = HostedWidget(
            appWidgetId = 99,
            providerPackage = "com.special.app",
            providerClass = "com.special.app.Provider",
            label = "Special: % & ; / ? # [HUD] ⚡ 日本語",
            heightDp = 200,
        )

        val serialized = WidgetSerializer.serialize(listOf(widget))
        val deserialized = WidgetSerializer.deserialize(serialized)

        assertEquals(1, deserialized.size)
        assertEquals("Special: % & ; / ? # [HUD] ⚡ 日本語", deserialized.first().label)
    }

    @Test
    fun deserialize_corruptOrMalformedInput_filtersOutInvalidTokens() {
        val malformed = "not_valid;12:pkg:cls;33:pkg2:cls2:180:Valid+Widget;missing_fields:1"
        val result = WidgetSerializer.deserialize(malformed)

        assertEquals(1, result.size)
        assertEquals(33, result.first().appWidgetId)
        assertEquals("Valid Widget", result.first().label)
    }

    @Test
    fun nextHeight_cyclesCorrectlyThroughAllowedDimensions() {
        assertEquals(200, HostedWidget.nextHeight(140))
        assertEquals(280, HostedWidget.nextHeight(200))
        assertEquals(140, HostedWidget.nextHeight(280))
        // Unknown height resets to first
        assertEquals(140, HostedWidget.nextHeight(999))
    }
}
