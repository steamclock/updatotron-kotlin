# Version Check

A library for doing checking of supported versions for a network service

Example basic usage:

```
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        setupVersionCheck()
    }

    private fun setupVersionCheck() {
        val versionChecker = VersionCheck(
            VersionCheckConfig(
             packageDetails = DefaultPackageDetails(
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    packageManager,
                    packageName),
            url = "https://myservice.com/api/version" // <-- Change this URL
          )
        )
       
        // Add the following line if you want the version check to be automatically
        // run whenever the app is "Started"
        ProcessLifecycleOwner.get().lifecycle.addObserver(versionChecker)

        // Add the following lines if you want a default alert dialog to handle the displayStates
        // and allow the user to jump out to the Play Store to get updates.
        val upgradeDialog = DefaultUpgradeDialog(versionChecker.displayStateFlow, packageName)
        registerActivityLifecycleCallbacks(upgradeDialog)
    }
}
```
If you wish to handle the call and results manually, you will need to call `versionCheck.runVersionCheck()` and collect from the StateFlows `statusFlow` and `displayStateFlow`.

Expected JSON format:
```
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

```
