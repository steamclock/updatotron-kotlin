# version-check-kotlin


### Deploying new versions

These steps are for developers looking to create a new release of the Version Check Kotlin library; if this does not pertain to you, please skip down to the **Installation** section.

Currently we are hosting the library on [Jitpack](https://jitpack.io/), to deploy a new version of the library:

1. Push all changes to master
2. From within the GitHub repo, navigate to the Code panel; on the right side should be a **Releases** section
3. Click on **Releases** (which should take you [here](https://github.com/steamclock/version-check-kotlin/releases))
4. Make note of the latest release version name (ie. v1.1)
5. Click the **Draft a new release** button on the right
6. Set the **Tag Version**; it's best use the last release version as a guide (ie. set as v1.2)
7. Set **Release Title** to match the version
8. Description is optional, could set changelog here if desired
9. Click the **Publish Release** button at the bottom
10. Verify on the [Jitpack page for the Steamclog project](https://jitpack.io/#steamclock/version-check-kotlin) that the new version is available
11. Update projects using Version Check Kotlin with the new version
