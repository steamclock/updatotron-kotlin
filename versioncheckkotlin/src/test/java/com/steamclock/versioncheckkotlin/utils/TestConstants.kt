package com.steamclock.versioncheckkotlin.utils

object TestConstants {
    const val validVersionDataJson = """
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
        """

    const val malformedJson = """
        { / Not what we want
        """

    const val invalidVersionDataJson = """
        { 
            "ios" : {
                "minimumVersion": "1.1",
                "blockedVersions": ["1.2.0", "1.2.1", "@301"],
                "latestTestVersion": "1.4.2@400"
            },
            "thisIsNamedWrong" : {
                "minimumVersion": "1.1",
                "blockedVersions": ["1.2.0", "1.2.1", "@301"],
                "latestTestVersion": "1.4.2@400"
            },
            "serverForceVersionFailure": false,
            "serverMaintenance": false
        }
        """
}