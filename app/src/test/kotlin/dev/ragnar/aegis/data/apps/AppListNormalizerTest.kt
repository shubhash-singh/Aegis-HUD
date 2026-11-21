package dev.ragnar.aegis.data.apps

import dev.ragnar.aegis.domain.apps.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppListNormalizerTest {

    @Test
    fun `sorts application labels alphabetically without case sensitivity`() {
        val apps = listOf(app("zulu"), app("Alpha"), app("bravo"))

        val result = AppListNormalizer.normalize(apps)

        assertEquals(listOf("Alpha", "bravo", "zulu"), result.map(AppInfo::label))
    }

    @Test
    fun `removes duplicate activity components`() {
        val duplicate = app(label = "Alpha", packageName = "dev.example.alpha")
        val apps = listOf(duplicate, duplicate.copy(label = "ALPHA"), app("Bravo"))

        val result = AppListNormalizer.normalize(apps)

        assertEquals(2, result.size)
        assertEquals(listOf("Alpha", "Bravo"), result.map(AppInfo::label))
    }

    @Test
    fun `returns an empty list when no apps are supplied`() {
        assertTrue(AppListNormalizer.normalize(emptyList()).isEmpty())
    }

    // --- F-005 Search Filtering Unit Tests (TEST_PLAN.md UT-005-01 through UT-005-04) ---

    @Test
    fun `UT-005-01 Filter by exact name returns correct app`() {
        val apps = listOf(app("Chrome"), app("Camera"), app("Calculator"))

        val result = AppListNormalizer.filter(apps, "Camera")

        assertEquals(1, result.size)
        assertEquals("Camera", result.first().label)
    }

    @Test
    fun `UT-005-02 Filter by partial name case-insensitive returns matches`() {
        val apps = listOf(app("Chrome"), app("Camera"), app("Calculator"), app("Settings"))

        val result = AppListNormalizer.filter(apps, "ca")

        assertEquals(2, result.size)
        assertEquals(listOf("Camera", "Calculator"), result.map(AppInfo::label))
    }

    @Test
    fun `UT-005-03 Empty query returns all apps`() {
        val apps = listOf(app("Chrome"), app("Camera"), app("Calculator"))

        val emptyResult = AppListNormalizer.filter(apps, "")
        val blankResult = AppListNormalizer.filter(apps, "   ")

        assertEquals(apps, emptyResult)
        assertEquals(apps, blankResult)
    }

    @Test
    fun `UT-005-04 Query with no matches returns empty list`() {
        val apps = listOf(app("Chrome"), app("Camera"), app("Calculator"))

        val result = AppListNormalizer.filter(apps, "Photoshop")

        assertTrue(result.isEmpty())
    }

    private fun app(
        label: String,
        packageName: String = "dev.example.${label.lowercase()}",
    ) = AppInfo(
        label = label,
        packageName = packageName,
        componentName = "$packageName/.MainActivity",
    )
}
