package com.steamclock.updatotron.utils

import com.steamclock.updatotron.Config

object TestConstants {

    object MockJson {
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

        const val serverMaintenanceActive = """
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
            "serverMaintenance": true
        }
        """

        const val serverForceVersionFailureActive = """
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
            "serverForceVersionFailure": true,
            "serverMaintenance": false
        }
        """
    }

    object Configs {
        // Test Config setups
        val validApp = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appOldVersion = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.0",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appVersionBlocked = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.2.1",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appBuildBlocked = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val jsonMalformed = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.malformedJson)
        )

        val jsonMalformedDevelopmentBuild = Config(
            packageDetails = MockPackageDetails.IsDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.malformedJson)
        )

        val jsonMissingAndroid = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.invalidVersionDataJson)
        )

        val jsonMissingAndroidDevelopmentBuild = Config(
            packageDetails = MockPackageDetails.IsDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.invalidVersionDataJson)
        )

        val latestTestVersionAvailable = Config(
            packageDetails = MockPackageDetails.TestUpdatesSupported(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val latestTestVersionNotApplicable = Config(
            packageDetails = MockPackageDetails.TestUpdatesNotSupported(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val serverMaintenanceActive = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.serverMaintenanceActive)
        )

        val serverForceVersionFailureActive = Config(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.5",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.serverForceVersionFailureActive)
        )
    }
}