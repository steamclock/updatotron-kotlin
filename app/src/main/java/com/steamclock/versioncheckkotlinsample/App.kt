package com.steamclock.versioncheckkotlinsample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import com.steamclock.versioncheckkotlin.VersionRepository
import com.steamclock.versioncheckkotlin.interfaces.DefaultUpgradeDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class App: Application(), Application.ActivityLifecycleCallbacks {

    // Required for versionCheck
    private val versionRepository = VersionRepository(
        appVersionName = BuildConfig.VERSION_NAME,
        appVersionCode = BuildConfig.VERSION_CODE,
        url = "https://doesn't_matter",
        urlFetcher = MainActivity.MockURLFetcher
    )
    private val upgradeDialog = DefaultUpgradeDialog()
    private val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        // We are using activity lifecycle callbacks as hooks to collect our version check
        // flows on each page.
        registerActivityLifecycleCallbacks(this)
    }

    //-----------------------------------------------------------------
    // Using ActivityLifecycleCallbacks to launch version checks and collect
    // state flows.
    //-----------------------------------------------------------------
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        /* No op */
    }

    override fun onActivityStarted(activity: Activity) {
        applicationScope.launch {
            // Called in onActivityStarted, so we should not have to use
            // repeatOnLifecycle(Lifecycle.State.STARTED) as our collection scope.
            versionRepository.displayStateFlow.collect {
                upgradeDialog.show(activity, it)
                Toast.makeText(activity, it.toString(), Toast.LENGTH_LONG).show()
            }
        }

        // As of right now the displayStateFlow collect does not trigger an initial update, so we
        // need to call manually. Doing this on the IO thread as we may be calling network APIs
        // in practice.
        //
        // Looking at documentation we may be able to trigger the update with each collection so
        // we do not actually have to call this manually? <-- todo
        applicationScope.launch(Dispatchers.IO) {
            // Delay to show that we do get the initial state value before we call the update
            delay(5000)
            versionRepository.runVersionCheck()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        upgradeDialog.showDialog()
    }

    override fun onActivityPaused(activity: Activity) {
        upgradeDialog.dismissDialog()
    }

    override fun onActivityStopped(activity: Activity) {
        /* No op */
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        /* No op */
    }

    override fun onActivityDestroyed(activity: Activity) {
        /* No op */
    }
}