package dev.ragnar.aegis

import dev.ragnar.aegis.data.diagnostics.AegisLogBuffer
import dev.ragnar.aegis.data.telemetry.BatteryNormalizer
import dev.ragnar.aegis.data.telemetry.NetworkNormalizer
import dev.ragnar.aegis.domain.diagnostics.LogLevel
import dev.ragnar.aegis.domain.settings.UserPreferences
import dev.ragnar.aegis.ui.home.visualization.NeuralGeometry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * Performance and Reliability Regression Test Suite (Phase 15 — F-016).
 *
 * Verifies key non-functional requirements:
 * - F-016-P1 / F-016-P3: Zero frame-by-frame memory allocation for 3D neural brain geometry
 * - F-016-P4 / F-016-P5: High-throughput telemetry normalizer performance under load
 * - F-016-P6: Thread safety and bound enforcement of the in-memory log buffer under concurrent load
 */
class PerformanceReliabilityAuditTest {

    @Test
    fun neuralGeometry_memoizationReturnsIdenticalInstanceForSameDimensions() {
        val width = 1080f
        val height = 2400f

        val geo1 = NeuralGeometry.build(width, height)
        val geo2 = NeuralGeometry.build(width, height)

        // Verifies zero-allocation memoization across consecutive 60fps render frames
        assertSame("Subsequent build calls with identical dimensions must return the cached instance", geo1, geo2)
    }

    @Test
    fun neuralGeometry_updatesWhenDimensionsChange() {
        val geo1 = NeuralGeometry.build(1080f, 2400f)
        val geo2 = NeuralGeometry.build(1440f, 3200f)

        assertNotSame("Different dimensions must produce updated geometry", geo1, geo2)
        assertEquals(540f, geo1.centerX, 0.01f)
        assertEquals(720f, geo2.centerX, 0.01f)
    }

    @Test
    fun logBuffer_concurrentThreadSafetyAndStrictCapacityBound() = runBlocking(Dispatchers.Default) {
        AegisLogBuffer.clear()
        val threadCount = 10
        val entriesPerThread = 50

        // Launch concurrent writers logging simultaneously
        val jobs = List(threadCount) { threadId ->
            launch {
                repeat(entriesPerThread) { i ->
                    AegisLogBuffer.log(
                        level = if (i % 2 == 0) LogLevel.INFO else LogLevel.DEBUG,
                        tag = "Thread-$threadId",
                        message = "High-throughput event $i",
                    )
                }
            }
        }
        jobs.joinAll()

        val snapshot = AegisLogBuffer.getEntries()
        assertEquals("Buffer must strictly cap at capacity under concurrent stress", AegisLogBuffer.MAX_CAPACITY, snapshot.size)
        assertTrue("All snapshot items must be valid", snapshot.all { entry -> entry.message.isNotEmpty() })
    }

    @Test
    fun telemetryNormalizers_performanceBenchmarkUnderLoad() {
        val iterations = 10_000

        val elapsedMs = measureTimeMillis {
            var currentRx = 1_000_000L
            var currentTx = 500_000L
            for (i in 1..iterations) {
                currentRx += 1024L
                currentTx += 512L
                NetworkNormalizer.calculateBandwidth(
                    currentRxBytes = currentRx,
                    previousRxBytes = currentRx - 1024L,
                    currentTxBytes = currentTx,
                    previousTxBytes = currentTx - 512L,
                    elapsedSeconds = 1.0,
                )
                BatteryNormalizer.normalize(
                    level = 85,
                    scale = 100,
                    status = 2,
                    plugged = 1,
                    temperatureTenths = 365,
                    voltageMv = 4100,
                    health = 2,
                )
            }
        }

        // 10,000 normalizations must execute in well under 500ms (typical JVM runtime < 50ms)
        assertTrue("Telemetry calculations must be ultra-fast (took ${elapsedMs}ms for 10k iterations)", elapsedMs < 500)
    }

    @Test
    fun userPreferences_defaultStateHasCleanMinimalFootprint() {
        val prefs = UserPreferences()
        assertTrue(prefs.swipeUpEnabled)
        assertTrue(prefs.swipeDownEnabled)
        assertTrue(prefs.longPressEnabled)
        assertTrue(prefs.favoriteApps.isEmpty())
        assertEquals(4, prefs.drawerGridColumns)
    }
}
