package com.steamclock.updatotron.utils

import com.steamclock.updatotron.UpdatotronConfig

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
        val validApp = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appOldVersion = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.0",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appVersionBlocked = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.2.1",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appBuildBlocked = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val jsonMalformed = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.malformedJson)
        )

        val jsonMalformedDevelopmentBuild = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.malformedJson)
        )

        val jsonMissingAndroid = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.invalidVersionDataJson)
        )

        val jsonMissingAndroidDevelopmentBuild = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.invalidVersionDataJson)
        )

        val latestTestVersionAvailable = UpdatotronConfig(
            packageDetails = MockPackageDetails.TestUpdatesSupported(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val latestTestVersionNotApplicable = UpdatotronConfig(
            packageDetails = MockPackageDetails.TestUpdatesNotSupported(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val serverMaintenanceActive = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.serverMaintenanceActive)
        )

        val serverForceVersionFailureActive = UpdatotronConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.5",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.serverForceVersionFailureActive)
        )
    }
}