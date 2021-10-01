package com.steamclock.versioncheckkotlin.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.cash.turbine.FlowTurbine
import junit.framework.TestCase
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * From https://proandroiddev.com/unit-testing-on-android-9c15632848c
 * 2021-09-27
 *
 * Helper method for testing LiveData objects, from
 * https://github.com/googlesamples/android-architecture-components.
 *
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
 * Once we got a notification via onChanged, we stop observing.
 */
@Throws(InterruptedException::class)
fun <T> getValue(liveData: LiveData<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    liveData.observeForever { o ->
        data[0] = o
        latch.countDown()
    }
    latch.await(2, TimeUnit.SECONDS)
    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    afterObserve.invoke()
    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}

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