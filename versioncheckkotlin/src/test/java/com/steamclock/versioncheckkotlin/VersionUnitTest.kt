package com.steamclock.versioncheckkotlin

import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception

class VersionUnitTest {

    /**
     * Tests situations where the parser should be successful.
     * Usually we would want to try/catch our Version instantiation, but since
     * we expect all these should pass the test I have omitted them in this test case.
     */
    @Test
    fun basicParse() {
        // Marketing, extra and build
        val version = Version("1.2.99-beta@400")
        assertTrue(version.marketingComponents.size == 3)
        assertTrue(version.marketingComponents[0] == 1)
        assertTrue(version.marketingComponents[1] == 2)
        assertTrue(version.marketingComponents[2] == 99)
        assertTrue(version.additionalText == "-beta")
        assertTrue(version.build == 400)
        assertTrue(version.description == "1.2.99-beta@400")
        assertFalse(version.isDevelopmemnt)

        // Marketing only
        val version2 = Version("2020.5")
        assertTrue(version2.marketingComponents.size == 2)
        assertTrue(version2.marketingComponents[0] == 2020)
        assertTrue(version2.marketingComponents[1] == 5)
        assertTrue(version2.additionalText.isEmpty())
        assertNull(version2.build)
        assertTrue(version2.description == "2020.5")
        assertFalse(version2.isDevelopmemnt)

        // Marketing and extra
        val version3 = Version("1.2.3-alpha")
        assertTrue(version3.marketingComponents.size == 3)
        assertTrue(version3.marketingComponents[0] == 1)
        assertTrue(version3.marketingComponents[1] == 2)
        assertTrue(version3.marketingComponents[2] == 3)
        assertTrue(version3.additionalText == "-alpha")
        assertNull(version3.build)
        assertTrue(version3.description == "1.2.3-alpha")
        assertFalse(version3.isDevelopmemnt)

        // Marketing and build
        val version4 = Version("4.99@12345")
        assertTrue(version4.marketingComponents.size == 2)
        assertTrue(version4.marketingComponents[0] == 4)
        assertTrue(version4.marketingComponents[1] == 99)
        assertTrue(version4.additionalText.isEmpty())
        assertTrue(version4.build == 12345)
        assertTrue(version4.description == "4.99@12345")
        assertFalse(version4.isDevelopmemnt)

        // Build only
        val version5 = Version("@999")
        assertTrue(version5.marketingComponents.isEmpty())
        assertTrue(version5.additionalText.isEmpty())
        assertTrue(version5.build == 999)
        assertTrue(version5.description == "@999")
        assertFalse(version5.isDevelopmemnt)

        // Dev build
        val version6 = Version("2020.2@0")
        assertTrue(version6.marketingComponents.size == 2)
        assertTrue(version6.marketingComponents[0] == 2020)
        assertTrue(version6.marketingComponents[1] == 2)
        assertTrue(version6.additionalText.isEmpty())
        assertNull(version6.build) // todo, should this actually still be 0?
        assertTrue(version6.description == "2020.2")
        assertTrue(version6.isDevelopmemnt)

    }

    /**
     * Tests situations where the parser should throw InvalidVersionString.
     */
    @Test
    fun failedParse() {
        fun attemptCreate(versionStr: String) {
            try {
                val version = Version(versionStr)
                assertFalse("Version init should have thrown an exception for $versionStr", true)
            } catch(e: Exception) {
                assertTrue(e is VersionCheckException.InvalidVersionString)
            }
        }

        attemptCreate("NotABuildNumber")
        attemptCreate("NotABuildNumber@100")
        attemptCreate("1.2.") // Ends with "."
        attemptCreate(".2.3") // Starts with "."
        attemptCreate("1.2.3@100@200") // Multiple build numbers
        attemptCreate("1.a") // Non-numeric marketing component
        attemptCreate("") // Empty string
        attemptCreate(".") // Empty marketing components
    }

    /**
     * Testing "==" / equals method
     */
    @Test
    fun equals() {
        val shouldBeEqual = listOf(
                Pair(Version("1.2.0"), Version("1.2.0")), // Exact
                Pair(Version("1.2"), Version("1.2.0")), // Omitted component
                Pair(Version("1.2.0-beta"), Version("1.2.0-beta")),  // With extra
                Pair(Version("1.2-beta"), Version("1.2.0-beta")), // With extra and omitted component
                Pair(Version("1.2.0@200"), Version("1.2.0@200")), // With build
                Pair(Version("1.2@200"), Version("1.2.0@200")), // With build and omitted component
                Pair(Version("1.2.0-beta@200"), Version("1.2.0-beta@200")), // With extra and build
                Pair(Version("1.2-beta@200"), Version("1.2.0-beta@200")) // With extra, build and and omitted component
        )

        val shouldNotBeEqual = listOf(
                Pair(Version("1.2.1"), Version("1.2.0")) // Exact
        )

        shouldBeEqual.forEach { assertTrue(it.first == it.second) }
        shouldNotBeEqual.forEach { assertFalse(it.first == it.second) }
    }

    /**
     * Testing compareTo (less than, greater than) implementation.
     * Usually we would want to try/catch our Version instantiation, but since
     * we expect all these should pass the test I have omitted them in this test case.
     */
    @Test
    fun compareTo() {
        fun orderedAscendingTrue(first: String, second: String) {
            assertTrue("$first should be < $second", Version(first) < Version(second))
        }

        fun orderedAscendingFalse(first: String, second: String) {
            assertFalse("$first should be not be < than $second", Version(first) < Version(second))
        }

        // a < b
        orderedAscendingTrue("1.2", "1.2.1")
        orderedAscendingTrue("1.2.2", "1.2.10")
        orderedAscendingTrue("1.2.4", "1.3.5")
        orderedAscendingTrue("1.2.4", "3")
        orderedAscendingTrue("3", "4.1")
        orderedAscendingTrue("1.2.4", "1.3.4")
        orderedAscendingTrue("1.2", "2")
        orderedAscendingTrue("1.2@30", "1.2@301")
        orderedAscendingTrue("1.2-b@300", "1.2-a@301")

        // a > b,
        orderedAscendingFalse("1.2.2", "1.2.1")
        orderedAscendingFalse("1.3@300", "1.2@200") // Bit of a trick question, build numbers are out of sequence, should probably warn in this case?
    }
}