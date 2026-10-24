package dev.ragnar.aegis

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Phase 1 placeholder instrumented test.
 *
 * Verifies that the instrumentation test runner is correctly configured
 * and the application package name matches the expected value.
 * Requires a connected device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("dev.ragnar.aegis", appContext.packageName)
    }
}
