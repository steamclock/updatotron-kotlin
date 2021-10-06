package com.steamclock.updatotron.interfaces

import java.net.URL

interface URLFetcher {
    fun getData(url: URL): String?
}

object NetworkURLFetcher: URLFetcher {
    override fun getData(url: URL): String {
       return url.readText()
    }
}