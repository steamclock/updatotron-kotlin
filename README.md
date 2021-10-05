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
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            url = "https://myservice.com/api/version" // <-- Change this URL
          )
        )
        
        // If you wish the VersionCheck to be automatically ran on the Application's OnStart
        // lifecycle call, add the following line:
        ProcessLifecycleOwner.get().lifecycle.addObserver(versionChecker)
        
        // If you wish to use the default alert dialog, add the following 2 lines:
        val upgradeDialog = DefaultUpgradeDialog(versionChecker.displayStateFlow)
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
