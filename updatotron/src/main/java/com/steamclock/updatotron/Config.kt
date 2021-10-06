package com.steamclock.updatotron

import com.steamclock.updatotron.interfaces.*

data class Config(
    val packageDetails: PackageDetails,
    val url: String,
    val urlFetcher: URLFetcher = NetworkURLFetcher,
    val versionDataConverter: VersionDataConverter = DefaultVersionDataConverter,
)