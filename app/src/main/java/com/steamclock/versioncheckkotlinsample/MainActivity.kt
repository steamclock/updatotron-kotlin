package com.steamclock.versioncheckkotlinsample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.steamclock.versioncheckkotlin.VersionCheckViewModel
import com.steamclock.versioncheckkotlin.VersionCheckViewModelFactory
import com.steamclock.versioncheckkotlin.VersionRepository
import com.steamclock.versioncheckkotlin.interfaces.URLFetcher
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val versionCheckVM: VersionCheckViewModel by viewModels {
        VersionCheckViewModelFactory(
            VersionRepository(
                appVersionName = BuildConfig.VERSION_NAME,
                appVersionCode = BuildConfig.VERSION_CODE,
                url = "https://doesn't_matter",
                urlFetcher = MockURLFetcher
            )
        )}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        versionCheckVM.status.observe(this, {
            Toast.makeText(this, "FROM MAIN_ACTIVITY ${it}", Toast.LENGTH_LONG).show()
        })

        versionCheckVM.runVersionCheck()
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