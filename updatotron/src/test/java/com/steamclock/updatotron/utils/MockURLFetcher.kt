package com.steamclock.updatotron.utils

import com.steamclock.updatotron.interfaces.URLFetcher
import java.net.URL

class MockURLFetcher(private val fauxJson: String) : URLFetcher {
    override fun getData(url: URL): String? {
        return fauxJson
    }
}