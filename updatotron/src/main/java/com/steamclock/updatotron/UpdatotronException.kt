package com.steamclock.updatotron

import java.lang.Exception

sealed class UpdatotronException(message: String? = null, cause: Throwable? = null): Exception(message, cause) {
    class InvalidVersionString(cause: Exception? = null): UpdatotronException("Invalid version string", cause)
}