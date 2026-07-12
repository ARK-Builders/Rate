# Workflows

This document describes GitHub Actions workflows. For local build prerequisites and fallback data setup, see [develop.md](develop.md).

## build.yml

`.github/workflows/build.yml` runs on pushes and pull requests to `main`.

For regular contributors, it:

1. Restores fallback rates and icons from `data/currency-icons-and-rates`.
2. Sets up JDK 21.
3. Validates the Gradle wrapper.
4. Runs `ktlintCheck`.
5. Decrypts signing and Google services files from GitHub secrets.
6. Builds the GitHub, Google Play, and WatchApp release APKs.
7. Uploads those APKs as GitHub Actions artifacts.
8. Runs Android lint and uploads lint reports.

For Dependabot, it runs a reduced debug build check.

This workflow verifies builds and keeps artifacts inside GitHub Actions. It does not create a GitHub Release and does not publish to Google Play.

## release.yml

`.github/workflows/release.yml` runs when a tag is pushed.

It:

1. Restores fallback rates and icons from `data/currency-icons-and-rates`.
2. Sets up JDK 21.
3. Validates the Gradle wrapper.
4. Decrypts signing and Google services files from GitHub secrets.
5. Builds release APKs.
6. Attaches APK artifacts to the GitHub Release for the tag.

This workflow is for GitHub APK releases. It does not publish to Google Play.

## fetch-data.yml

`.github/workflows/fetch-data.yml` updates rates and crypto icons on the `data/currency-icons-and-rates` branch. It can run on schedule or manually through `workflow_dispatch`.

It uses `scripts/update_crypto_data.py` and the `OPENEXCHANGERATES_API_KEY` secret to refresh bundled data, then pushes changes back to the data branch.
