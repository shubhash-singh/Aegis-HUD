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

    private fun app(
        label: String,
        packageName: String = "dev.example.${label.lowercase()}",
    ) = AppInfo(
        label = label,
        packageName = packageName,
        componentName = "$packageName/.MainActivity",
    )
}
