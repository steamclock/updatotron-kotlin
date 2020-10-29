package com.steamclock.versioncheckkotlin

import java.lang.Exception

sealed class VersionCheckException(message: String? = null, cause: Throwable? = null): Exception(message, cause) {
    class InvalidVersionString(cause: Exception? = null): VersionCheckException("Invalid version string", cause)
}