package com.steamclock.updatotron.interfaces

import com.steamclock.updatotron.models.Version
import com.steamclock.updatotron.models.PlatformVersionData
import com.steamclock.updatotron.models.VersionData
import org.json.JSONArray
import org.json.JSONObject

fun JSONArray.toVersionSet(): Set<Version> {
    val resultSet = HashSet<Version>()
    for (i in 0 until length()) {
        resultSet.add(Version(optString(i)))
    }
    return resultSet
}

interface VersionDataConverter {
    fun parse(jsonStr: String): VersionData
}

/**
 * DefaultVersionDataConverter Expected Format
 *
{
    "ios" : {
        "minimumVersion": "1.1",
        "blockedVersions": ["1.2.0", "1.2.1", "@301"],
        "latestTestVersion": "1.4.2@400"
    },
    "android" : {
        "minimumVersion": "1.1",
        "blockedVersions": ["1.2.0", "1.2.1", "@301"],
        "latestTestVersion": "1.4.2@400"
    },
    "serverForceVersionFailure": false,
    "serverMaintenance": false
}
 */
object DefaultVersionDataConverter: VersionDataConverter {
    override fun parse(jsonStr: String): VersionData {
        val jsonObj = JSONObject(jsonStr)

        // Android Platform
        val androidObj: JSONObject = jsonObj.getJSONObject("android")
        val minVers = Version(androidObj.getString("minimumVersion"))
        val blockedVersSet = androidObj.getJSONArray("blockedVersions").toVersionSet()
        val latestTestVers = Version(androidObj.getString("latestTestVersion"))

        // Server Flags
        val serverForceVersionFailure = jsonObj.getBoolean("serverForceVersionFailure")
        val serverMaintenance = jsonObj.getBoolean("serverMaintenance")

        return VersionData(
            android = PlatformVersionData(
                minimumVersion = minVers,
                blockedVersions = blockedVersSet,
                latestTestVersion = latestTestVers
            ),
            serverForceVersionFailure = serverForceVersionFailure,
            serverMaintenance = serverMaintenance
        )
    }
}
