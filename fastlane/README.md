fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android internal

```sh
[bundle exec] fastlane android internal
```

Build & upload a Release AAB to the Internal testing track

Options: skip_bump:true | code_only:true | promote:<code> | skip_upload:true | skip_push:true

### android closed

```sh
[bundle exec] fastlane android closed
```

Build & upload a Release AAB to Closed testing (Alpha)

Options: skip_bump:true | code_only:true | promote:<code> | skip_upload:true | skip_push:true

### android production

```sh
[bundle exec] fastlane android production
```

Build & upload a Release AAB to Production

Options: skip_bump:true | code_only:true | promote:<code> | skip_upload:true | skip_push:true

### android internal_share

```sh
[bundle exec] fastlane android internal_share
```

Build a Release APK and upload to Internal App Sharing (APK only, no bump)

### android retry_production_upload

```sh
[bundle exec] fastlane android retry_production_upload
```

Retry Play upload for existing release artifacts without rebuilding/bumping

### android test

```sh
[bundle exec] fastlane android test
```

Run unit tests

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
