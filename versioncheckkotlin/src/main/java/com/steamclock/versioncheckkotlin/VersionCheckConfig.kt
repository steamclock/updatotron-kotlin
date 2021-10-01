package com.steamclock.versioncheckkotlin

import com.steamclock.versioncheckkotlin.interfaces.DefaultVersionDataConverter
import com.steamclock.versioncheckkotlin.interfaces.NetworkURLFetcher
import com.steamclock.versioncheckkotlin.interfaces.URLFetcher
import com.steamclock.versioncheckkotlin.interfaces.VersionDataConverter

data class VersionCheckConfig(
    val appVersionName: String,
    val appVersionCode: Int,
    val url: String,
    val urlFetcher: URLFetcher = NetworkURLFetcher,
    val versionDataConverter: VersionDataConverter = DefaultVersionDataConverter
)