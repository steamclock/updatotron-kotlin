package com.steamclock.versioncheckkotlin.utils

import com.steamclock.versioncheckkotlin.VersionCheckConfig
import com.steamclock.versioncheckkotlin.interfaces.DefaultPackageDetails
import com.steamclock.versioncheckkotlin.interfaces.PackageDetails

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
    }

    object VersionCheckConfig {
        // Test Config setups
        val validApp = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appOldVersion = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.0",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appVersionBlocked = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.2.1",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val appBuildBlocked = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val jsonMalformed = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.malformedJson)
        )

        val jsonMalformedDevelopmentBuild = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.malformedJson)
        )

        val jsonMissingAndroid = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsNotDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.invalidVersionDataJson)
        )

        val jsonMissingAndroidDevelopmentBuild = VersionCheckConfig(
            packageDetails = MockPackageDetails.IsDevelopmentBuild(
                appVersionName = "1.1",
                appVersionCode = 301
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.invalidVersionDataJson)
        )

        val latestTestVersionAvailable = VersionCheckConfig(
            packageDetails = MockPackageDetails.TestUpdatesSupported(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )

        val latestTestVersionNotApplicable = VersionCheckConfig(
            packageDetails = MockPackageDetails.TestUpdatesNotSupported(
                appVersionName = "1.3",
                appVersionCode = 400
            ),
            url = "https://this-doesnt-matter",
            urlFetcher = MockURLFetcher(MockJson.validVersionDataJson)
        )
    }
}