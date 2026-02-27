# NULVEX Release Checklist

Last updated: 2026-02-27

## Build and Signing

- [x] `:app:assembleDebug`
- [x] `:app:assembleRelease`
- [ ] verify production signing material in `secrets/keystore.properties`
- [ ] verify final version bump and changelog policy

## Test Gates

- [x] `./gradlew test`
- [x] `./gradlew :app:assembleDebugAndroidTest`
- [x] `./gradlew connectedAndroidTest` (emulator)
- [ ] manual release smoke on target release environment

## Security and Data Flows

- [x] panic wipe automated tests passing
- [x] decoy/auth automated tests passing
- [x] deep-link sanitization tests passing
- [x] backup/share import validation tests passing
- [ ] manual verification of panic+decoy on release build

## Store Assets and Policy

- [x] title/short/full description present in fastlane metadata
- [x] screenshots present in fastlane metadata
- [x] security whitepaper draft prepared
- [x] privacy policy draft prepared
- [ ] final legal/product owner review and approval

## Operational

- [ ] final dependency vulnerability triage before publish
- [ ] publish signed artifact to chosen track
- [ ] post-release monitoring plan and rollback criteria
