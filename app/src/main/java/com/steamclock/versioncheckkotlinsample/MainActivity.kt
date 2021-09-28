package com.steamclock.versioncheckkotlinsample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.steamclock.versioncheckkotlin.VersionCheckViewModel
import com.steamclock.versioncheckkotlin.interfaces.DefaultUpgradeDialog
import com.steamclock.versioncheckkotlin.interfaces.URLFetcher
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val versionCheck: VersionCheckViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupObservers()
        runVersionCheck()
    }

    private fun runVersionCheck() {
        // Setup dialog handler
        val defaultDialogHandler = DefaultUpgradeDialog(this)
        lifecycle.addObserver(defaultDialogHandler)

        // Run version check
        versionCheck.runVersionCheck(
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE.toString(),
            url = "https://doesn't_matter",
            urlFetcher = MockURLFetcher,
            upgradeDialog = defaultDialogHandler
        )
    }

    private fun setupObservers() {
        versionCheck.status.observe(this, {
            Log.d("Check", it.toString())
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        })
    }

    object MockURLFetcher: URLFetcher {
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
}