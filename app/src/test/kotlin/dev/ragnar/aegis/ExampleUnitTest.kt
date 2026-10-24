package dev.ragnar.aegis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Phase 1 placeholder unit test.
 *
 * Verifies that the JVM test harness is correctly configured.
 * Meaningful tests will be added in subsequent phases.
 */
class ExampleUnitTest {

    @Test
    fun `test harness is operational`() {
        // Simple assertion to confirm the test runner works.
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `aegis package name is correct`() {
        // Verify the package name matches the project convention.
        val expectedPackage = "dev.ragnar.aegis"
        assertTrue(
            "ExampleUnitTest must be in package $expectedPackage",
            this::class.java.name.startsWith(expectedPackage)
        )
    }
}
