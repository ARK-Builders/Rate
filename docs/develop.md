# Development

This document covers local build prerequisites and development data setup.

## GitHub Packages authentication

The project depends on packages hosted in GitHub Packages. To build locally, create a fine-grained personal access token in GitHub:

1. Open [GitHub personal access tokens](https://github.com/settings/personal-access-tokens/new).
2. Create a fine-grained token with the minimum needed access: **Public repositories** (read-only).
3. Add it to `local.properties` in the project root:

```properties
gpr.token=your_github_token
```

Gradle reads this value in `settings.gradle.kts`. If `gpr.token` is not present, Gradle falls back to the `GITHUB_TOKEN` environment variable.

## Restore bundled fallback data

All currency icons and rates are shipped with the app bundle. During first app launch, rates will be updated, but in-bundle rates serve as a fallback.

The branch `data/currency-icons-and-rates` is used to update data automatically. The `main` branch does not contain generated crypto icons or bundled rate files.

These files are required for the app's bundled fallback path. A build can still pass without fresh local fallback data, but the app may ship without the intended offline rates, update timestamp, or crypto icons. Always restore them before local release checks or Play Store release builds.

For local testing, restore the latest remote data:

```sh
git fetch origin data/currency-icons-and-rates
git restore --source=FETCH_HEAD --worktree \
  core/data/src/main/assets/crypto-rates.json \
  core/data/src/main/assets/fiat-rates.json \
  core/data/src/main/assets/updatedAt \
  cryptoicons/src/main/res/drawable
```

Do not commit `crypto-rates.json`, `fiat-rates.json`, `updatedAt`, or `cryptoicons/src/main/res/drawable`.
