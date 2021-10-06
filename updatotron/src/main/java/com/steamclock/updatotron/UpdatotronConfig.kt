package com.steamclock.updatotron

import com.steamclock.updatotron.interfaces.*

data class UpdatotronConfig(
    val packageDetails: PackageDetails,
    val url: String,
    val urlFetcher: URLFetcher = NetworkURLFetcher,
    val versionDataConverter: VersionDataConverter = DefaultVersionDataConverter,
)