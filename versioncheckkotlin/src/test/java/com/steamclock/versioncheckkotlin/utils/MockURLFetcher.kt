package com.steamclock.versioncheckkotlin.utils

import com.steamclock.versioncheckkotlin.interfaces.URLFetcher
import java.net.URL

class MockURLFetcher(private val fauxJson: String) : URLFetcher {
    override fun getData(url: URL): String? {
        return fauxJson
    }
}