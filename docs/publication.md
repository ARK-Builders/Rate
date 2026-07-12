# Publication

This document covers post-build publication work: release notes, store and website listings, screenshots, and announcements.

## Step 1: Prepare release notes

Write release notes from the user point of view. Focus on visible product changes, important fixes, compatibility notes, and anything users should know before updating.

Prepare three variants from the same source summary:

1. GitHub Release notes: more detailed, with useful links to pull requests, issues, and artifacts.
2. Google Play release notes: shorter, localized, and suitable for the Play Console "What's new in this release?" field.
3. Website release notes: user-facing summary for the ARK Rate listing on `ark-builders.dev/apps/rate`.

For GitHub, use generated release notes as a draft when possible, then edit them. GitHub-generated release notes include merged pull requests, contributors, and a full changelog link; keep those details useful for technical users.

For Google Play, keep each locale under the Play Console limit of 500 Unicode characters. Do not use Play Store release notes for promotion or calls to action; describe what changed in the release.

Maintain Play Store release notes for all languages supported by the app UI. Keep this list aligned with the app's localized resources instead of hard-coding a fixed language set in the release process.

Use this Play Console format:

```xml
<en-US>
English release notes, up to 500 Unicode characters.
</en-US>
<ru-RU>
Russian release notes, up to 500 Unicode characters.
</ru-RU>
```

## Step 2: Update Play Store listing

Update the Play Store description and screenshots when the release changes the product story, supported devices, major workflows, or visual appearance.

For screenshots, show the current app experience. For Wear OS releases, include Wear OS screenshots when the release adds or significantly changes watch functionality.

Keep Play Store assets aligned with Google Play preview asset guidance: use real in-app UI, avoid outdated/time-sensitive copy, avoid ranking or promotional claims, and localize text overlays when used.

## Step 3: Update website listing

Update the ARK Rate listing on `ark-builders.dev/apps/rate` when Play Store copy or screenshots change.

Until the website supports structured per-release notes, keep the website listing current and create a follow-up task when a release needs a dedicated changelog entry. Once the website supports release notes, publish the same user-facing summary prepared in Step 1.

## Step 4: Announce the release

Prepare short announcement copy for Telegram, Twitter/X, Bluesky, LinkedIn, and Instagram.

Use the same core message everywhere, adapted to the channel:

1. Lead with the main user-visible change.
2. Mention the platform if relevant, such as Wear OS.
3. Add one link to the Play Store or website.
4. Keep claims factual and avoid promising unsupported features.
5. Use screenshots or short videos when the release changes UI or introduces a new workflow.

## References

- GitHub generated release notes: https://docs.github.com/en/repositories/releasing-projects-on-github/automatically-generated-release-notes
- Google Play release preparation and release notes: https://support.google.com/googleplay/android-developer/answer/9859348
- Google Play preview assets and screenshots: https://support.google.com/googleplay/android-developer/answer/9866151
