package com.steamclock.updatotronsample

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.steamclock.updatotron.DefaultUpgradeDialog
import com.steamclock.updatotron.Updatotron
import com.steamclock.updatotron.UpdatotronConfig
import com.steamclock.updatotron.interfaces.DefaultPackageDetails
import com.steamclock.updatotron.interfaces.URLFetcher
import java.net.URL

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setupUpdatotron()
    }

    private fun setupUpdatotron() {
        val updatotron = Updatotron(
            UpdatotronConfig(
                packageDetails = DefaultPackageDetails(
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    packageManager,
                    packageName
                ),
                url = "https://doesn't_matter",
                urlFetcher = MockURLFetcher
            )
        )

        // Add the following line if you want the version check to be automatically
        // run whenever the app is "Started"
        ProcessLifecycleOwner.get().lifecycle.addObserver(updatotron)

        // Add the following lines if you want a default alert dialog to handle the displayStates
        // and allow the user to jump out to the Play Store to get updates.
        val upgradeDialog = DefaultUpgradeDialog(updatotron.displayStateFlow, packageName)
        registerActivityLifecycleCallbacks(upgradeDialog)
    }
}

object MockURLFetcher : URLFetcher {
    override fun getData(url: URL): String? {
        return """
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
    }
}