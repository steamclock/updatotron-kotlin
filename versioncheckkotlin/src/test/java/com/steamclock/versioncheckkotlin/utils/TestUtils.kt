package com.steamclock.versioncheckkotlin.utils

import app.cash.turbine.FlowTurbine
import junit.framework.TestCase
import java.lang.Exception

/**
 * Helper method for testing hot flows that helps us confirm that no more emits have been made
 * to the given flow. Will fail if an emit was collected before the timeout period (1s)
 */
suspend fun <T> FlowTurbine<T>.confirmLastEmit() {
    try {
        val item = awaitItem()
        TestCase.fail("Subsequent emit was found: $item")
    } catch (e: Exception) {
        // Success, timeout means no more items were found.
    }
    cancelAndIgnoreRemainingEvents()
}