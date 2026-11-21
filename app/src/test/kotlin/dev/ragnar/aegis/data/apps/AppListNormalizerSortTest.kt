package dev.ragnar.aegis.data.apps

import dev.ragnar.aegis.domain.apps.AppInfo
import dev.ragnar.aegis.domain.apps.AppSortOrder
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [AppListNormalizer.sort] — F-011 (Application Organization).
 */
class AppListNormalizerSortTest {

    private fun createApp(
        label: String,
        packageName: String = "dev.ragnar.$label",
        componentName: String = "dev.ragnar.$label/.MainActivity",
        installTimeMillis: Long = 0L,
    ) = AppInfo(label, packageName, componentName, installTimeMillis)

    @Test
    fun `UT-011-02 sort by AZ orders ascending by label with case insensitivity`() {
        val apps = listOf(
            createApp("YouTube"),
            createApp("camera"),
            createApp("Browser"),
            createApp("Alarm"),
        )

        val result = AppListNormalizer.sort(apps, AppSortOrder.AZ)

        assertEquals(listOf("Alarm", "Browser", "camera", "YouTube"), result.map { it.label })
    }

    @Test
    fun `UT-011-03 sort by ZA orders descending by label with case insensitivity`() {
        val apps = listOf(
            createApp("Alarm"),
            createApp("Browser"),
            createApp("camera"),
            createApp("YouTube"),
        )

        val result = AppListNormalizer.sort(apps, AppSortOrder.ZA)

        assertEquals(listOf("YouTube", "camera", "Browser", "Alarm"), result.map { it.label })
    }

    @Test
    fun `UT-011-04 sort by FavoritesFirst puts pinned components first then alphabetical`() {
        val app1 = createApp("Alarm", componentName = "dev.ragnar.alarm/.Main")
        val app2 = createApp("Browser", componentName = "dev.ragnar.browser/.Main")
        val app3 = createApp("Camera", componentName = "dev.ragnar.camera/.Main")
        val app4 = createApp("YouTube", componentName = "dev.ragnar.youtube/.Main")

        val apps = listOf(app1, app2, app3, app4)
        val favorites = setOf(app4.componentName, app2.componentName) // YouTube and Browser pinned

        val result = AppListNormalizer.sort(apps, AppSortOrder.FavoritesFirst, favorites)

        // Pinned apps (Browser, YouTube) appear first in alphabetical order, followed by Alarm, Camera
        assertEquals(listOf("Browser", "YouTube", "Alarm", "Camera"), result.map { it.label })
    }

    @Test
    fun `UT-011-05 sort by InstallDate orders by newest install time descending`() {
        val appOld = createApp("OldApp", installTimeMillis = 1000L)
        val appMid = createApp("MidApp", installTimeMillis = 5000L)
        val appNew = createApp("NewApp", installTimeMillis = 9000L)

        val apps = listOf(appOld, appNew, appMid)

        val result = AppListNormalizer.sort(apps, AppSortOrder.InstallDate)

        assertEquals(listOf("NewApp", "MidApp", "OldApp"), result.map { it.label })
    }

    @Test
    fun `UT-011-06 tie breaking uses packageName and componentName`() {
        val appA1 = createApp("Same", packageName = "dev.a", componentName = "dev.a/.One")
        val appA2 = createApp("Same", packageName = "dev.b", componentName = "dev.b/.Two")

        val resultAZ = AppListNormalizer.sort(listOf(appA2, appA1), AppSortOrder.AZ)
        assertEquals("dev.a", resultAZ[0].packageName)
        assertEquals("dev.b", resultAZ[1].packageName)
    }
}
