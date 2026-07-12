# Release

GitHub APK releases and Play Store releases are separate paths.

Before any release, make sure the app builds locally. Set up local build prerequisites as described in [develop.md](develop.md).

## Pre-release build check

Restore fallback rates and icons as described in [develop.md](develop.md#restore-bundled-fallback-data).

Run the local checks:

```sh
./gradlew ktlintCheck lint assembleDebug
```

## GitHub APK release

Use this path to publish APK artifacts through GitHub Releases.

1. Update `versionCode` and `versionName` in `app/build.gradle.kts`.
2. Push the release commit.
3. Create and push a version tag:

```sh
git tag vX.Y.Z
git push origin vX.Y.Z
```

Pushing the tag triggers `release.yml`; see [workflows.md](workflows.md#releaseyml) for what that workflow does.

## Play Store release

Use this path for the production Play Store release. Play Store publishing is done manually from Android Studio on the release manager's machine.

1. Update `versionCode` and `versionName` in `app/build.gradle.kts`.
2. Put `keystore.jks` in the project root from the secret storage.
3. Put the production `app/google-services.json` from the secret storage.
4. Restore bundled fallback rates and icons as described in [develop.md](develop.md#restore-bundled-fallback-data).
5. Open the project in Android Studio and create the signed Google Play release artifact using the production signing/config files.
6. Upload the generated Play Store artifact through Google Play Console.

Do not commit `keystore.jks`, production `app/google-services.json`, fallback rates, `updatedAt`, generated crypto icons, or generated release artifacts.
