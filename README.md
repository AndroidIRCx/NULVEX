# NULVEX

[![Android Unit Tests](https://github.com/AndroidIRCx/NULVEX/actions/workflows/android-unit-tests.yml/badge.svg)](https://github.com/AndroidIRCx/NULVEX/actions/workflows/android-unit-tests.yml)
[![Google Play Closed Testing](https://img.shields.io/badge/Google%20Play-Closed%20Testing-01875f?logo=googleplay&logoColor=white)](https://play.google.com/apps/testing/com.androidircx.nulvex)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

**Offline-first encrypted notes vault for Android.**
Built for secure local notes with strong encryption, clean UX, and optional cloud-era monetization/testing integrations.

---

## Features

### Security
- **Encrypted vault** — all notes stored as XChaCha20-Poly1305 ciphertext; plaintext never touches disk
- **Argon2id KDF** — PIN/password stretched with 256 MiB memory, 3 iterations, parallelism 2
- **Android Keystore** — master key wrapped in hardware-backed Keystore (StrongBox when available)
- **SQLCipher database** — encrypted SQLite; DB key derived at runtime, never stored in plaintext
- **Decoy vault** — separate encrypted database behind a different PIN; coercion-resistant
- **Panic wipe** — hold-to-confirm (2.5 s) destroys all vault data and keys immediately
- **PIN brute-force lockout** — 5 wrong attempts → 30 s, 7 → 2 min, 10 → 10 min; persists across restarts
- **Screenshot protection** — `FLAG_SECURE` on all windows; blocked in recents and screen capture
- **Biometric unlock** — fingerprint/device credential via BiometricPrompt; master key encrypted in Keystore
- **Auto-lock** — configurable inactivity timeout (off / 30 s / 1 min / 5 min / 10 min)
- **Memory zeroing** — key material zeroed after use

### Notes
- **Read-once** — note is permanently destroyed after the first open (burn after reading)
- **Auto-expiry** — set a TTL per note; expired notes swept automatically via WorkManager
- **Note editing** — edit in place; SAVE re-encrypts, CANCEL reverts
- **Pinned notes** — pin important notes to the top of the list
- **Checklists** — toggle, reorder, add and remove checklist items
- **Labels** — tag and filter notes by label
- **Encrypted note share links** — export note as encrypted `.nulvex` package and share remote link

### UX
- **Secure PIN pad** — custom circular numpad, no system keyboard; dot indicator, haptic feedback
- **Light / Dark / System theme**
- **Settings search** — section/options search with clear button
- **Collapsible settings sections** — cleaner navigation as settings grow
- **Rewards & Ads first** — monetization section moved to top of Settings

### Keys, Backup & Share
- **Keys Manager (not Pro-gated)** — manage imported OpenPGP and XChaCha keys
- **Key import channels** — manual text, QR scan, NFC tag
- **Key Manager export/import** — full keys storage export as plaintext or password-encrypted package
- **Local encrypted backup** — export/import vault backup file (`.nulvxbk`) directly on device
- **Remote media backup (Pro)** — upload encrypted backup blobs to media server and restore via saved link/token

File types:
- `.nulvex` — encrypted note-share package
- `.nulvxbk` — encrypted vault backup package
- `.nulvxkeys` — key-manager storage package

| Extension | MIME | Purpose | Example |
|---|---|---|---|
| `.nulvex` | `application/x-nulvex-note` | Encrypted single-note share package | `note_1739982000000.nulvex` |
| `.nulvxbk` | `application/x-nulvex-backup` | Encrypted full-vault backup package | `nulvex_backup_1739982000000.nulvxbk` |
| `.nulvxkeys` | `application/x-nulvex-keys` | Key Manager export/import package | `nulvex_keys_1739982000000.nulvxkeys` |

---

## Download (Closed testing)

The app is currently in **Google Play Closed Testing** and can be installed only through the Play testing flow.

Join Google Groups first:
- https://groups.google.com/g/androidircx
- https://groups.google.com/g/testers-community

Then join testing and install:
- Join on Android & install: https://play.google.com/store/apps/details?id=com.androidircx.nulvex
- Join on the web: https://play.google.com/apps/testing/com.androidircx.nulvex

Troubleshooting:
- Use the same Google account for Google Groups and Play Store.
- If opt-in does not appear immediately, wait a few minutes and reopen Play Store.

---

## Telemetry & Ads

- Firebase Analytics is enabled.
- Google Mobile Ads SDK (AdMob) is integrated.
- Crashlytics is not enabled in the current Gradle config.

---

## Cryptography

| Layer | Primitive |
|---|---|
| KDF | Argon2id (256 MiB / 3 iter / p=2) |
| Subkey derivation | HKDF-SHA-256 |
| Note encryption | XChaCha20-Poly1305 (via Google Tink) |
| Keystore wrapping | AES-256-GCM (Android Keystore / StrongBox) |
| Biometric wrapping | AES-256-GCM cipher-based BiometricPrompt |
| Hybrid KEM | X25519 + ML-KEM-768 |

Key hierarchy:

```
PIN / password
    └─ Argon2id ──► Master Seed
                        └─ HKDF ──► DB key (SQLCipher)
                                    Note encryption key(s)
                                    Sync envelope key(s)
```

Biometrics authenticate the user but do not derive cryptographic keys — the master key is stored encrypted by a Keystore-backed AES cipher locked to biometric authentication.

---

## Requirements

- Android 8.0+ (API 26)
- Kotlin 2.3.10
- AGP 9.0.1 / Gradle 9.3.1

---

## Build

```bash
# Debug
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Release AAB
./gradlew bundleRelease
```

Build outputs:
- APK: `app/build/outputs/apk/release/`
- AAB: `app/build/outputs/bundle/release/`

Release signing requires a gitignored `keystore.properties` file next to the project root:

```properties
storeFile=path/to/your.keystore
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Without it the build compiles but produces an unsigned AAB.

---

## Transifex (File-Based)

Set credentials in `secrets/transifex.env`:

```env
TRANSIFEX_API_TOKEN=...
TRANSIFEX_TOKEN=... # optional, fallback auth
TRANSIFEX_PROJECT_NAME=NULVEX
TRANSIFEX_ORG_SLUG=androidircx # optional; auto-resolved if omitted
TRANSIFEX_RESOURCE_SLUG=android-strings
```

Windows:

```powershell
./gradlew :app:transifexPushSources
./gradlew :app:transifexPullTranslations
```

Linux / WSL:

```bash
./scripts/transifex/push_sources.sh
./scripts/transifex/pull_translations.sh
```

Optional build-time pull:

```bash
./gradlew assembleDebug -PtxPullOnBuild=true
```

---

## Release

Releases are automated with Fastlane. Use Bundler-installed fastlane:

```bash
bundle exec fastlane closed
```

What it does:
1. `assembleRelease` — builds release APK for local/device testing
2. `bundleRelease` — builds release AAB for Play upload and auto-increments `versionCode` + `versionName`
3. `git commit` + `push` — commits `version.properties` and pushes to `main`
4. `upload_to_play_store` — uploads only AAB + R8 mapping to the **alpha** track (`skip_upload_apk: true`)

Notes:
- Lane currently runs Gradle with `--no-configuration-cache` for stability with custom version bump task.
- If upload fails after step 3, the version bump commit is already pushed.

See [`fastlane/README.md`](fastlane/README.md) for all available lanes.

Fastlane requires a Google Play service account JSON key. Configure the path in `fastlane/Appfile`:

```ruby
json_key_file("secrets/play-service-account.json")
package_name("com.androidircx.nulvex")
```

Follow the [supply setup guide](https://docs.fastlane.tools/actions/supply/#setup) to generate the service account key in Google Play Console.

---

## Project Structure

```
app/src/main/java/com/androidircx/nulvex/
├── MainActivity.kt           # FLAG_SECURE, biometric wiring
├── VaultServiceLocator.kt    # Service locator / DI
├── NulvexApp.kt
├── crypto/
│   ├── NoteCrypto.kt         # Encryption interface
│   └── XChaCha20Poly1305NoteCrypto.kt
├── data/
│   ├── NoteDao.kt            # Room DAO (suspend)
│   ├── NoteEntity.kt         # @Entity — id, ciphertext, expiresAt, readOnce, deleted
│   ├── NoteRepository.kt     # Encrypt / decrypt round-trip
│   ├── VaultService.kt       # CRUD facade
│   ├── SelfDestructService.kt# Sweep expired notes, VACUUM
│   └── NulvexDatabase.kt     # SQLCipher-backed RoomDatabase
├── security/
│   ├── Argon2idKdf.kt        # PIN → master seed
│   ├── VaultKeyManager.kt    # Key derivation + Keystore wrapping
│   ├── HybridKemService.kt   # X25519 + ML-KEM-768 hybrid key exchange
│   ├── VaultAuthService.kt   # PIN verify, decoy, attempt limiting
│   ├── PanicWipeService.kt   # Full wipe + decoy-only wipe
│   ├── BiometricKeyStore.kt  # Encrypt/decrypt master key via BiometricPrompt
│   └── AppPreferences.kt     # Lock timeout, lockout state, theme
├── ui/
│   ├── MainScreen.kt         # All Compose screens
│   ├── MainViewModel.kt      # Single UiState, all app logic
│   └── theme/
├── pro/
│   ├── SharedKeyStore.kt         # Key manager storage + Keystore protection
│   ├── OpenPgpSupport.kt         # OpenPGP armored key parsing
│   ├── EncryptedBackupService.kt # Local/remote encrypted backup + note-share upload
│   ├── BackupRegistryStore.kt    # Saved remote backup metadata and links
│   └── KeyManagerBackupCodec.kt  # Password-encrypted key-manager export/import
└── work/
    └── SelfDestructWorker.kt # WorkManager: sweep expired notes
```

---

## Threat Model (summary)

**Protected against:**
- Physical access to device at rest
- Full filesystem access (rooted / adb backup)
- Network observer during sync (zero-knowledge model)
- Coercion / forced unlock (decoy vault + panic wipe)
- Device theft without unlock credentials

**Out of scope (v1):**
- Compromise of device while unlocked and running
- Full-device malware with memory access
- Hardware side-channel attacks

---

## Testing

Current local status (February 19, 2026):
- `./gradlew test` passes
- `./gradlew connectedAndroidTest` requires a running emulator/device
- JaCoCo report: `./gradlew :app:jacocoDebugUnitTestReport`
- JaCoCo gate: `./gradlew :app:jacocoDebugUnitTestCoverageVerification` (default line threshold `0.05`, override with `-Pcoverage.minimum.line=...`)

**JVM unit tests** (`./gradlew test`, CI-ready):

| Suite | Tests | What it covers |
|---|---|---|
| `XChaCha20Poly1305NoteCryptoTest` | 8 | AEAD round-trip, nonce uniqueness, tamper detection |
| `HkdfTest` | 8 | RFC 5869 vectors, key length flexibility, domain separation |
| `VaultKeyManagerTest` | 11 | Master / DB / note key derivation, layer isolation |
| `NoteRepositoryTest` | 11 | Encrypt-on-write, decrypt-on-read, secure delete (ciphertext zeroing) |
| `SelfDestructServiceTest` | 7 | Expired note sweep, ciphertext zeroing, VACUUM |
| `VaultSessionManagerTest` | 5 | Session state machine, null-state thread safety |
| `PlayBillingProductsTest` | 2 | One-time product IDs and INAPP query mapping |

**Instrumented tests** (`./gradlew connectedAndroidTest`, requires device or emulator):

| Suite | Tests | What it covers |
|---|---|---|
| `Argon2idKdfTest` | 8 | Native Argon2id: correct output length, determinism, salt/password sensitivity |
| `VaultAuthServiceTest` | 12 | PIN setup/verify, real vs decoy resolution, clearDecoyPin, random salt per hash |
| `NotePayloadCodecTest` | 12 | JSON codec: round-trip, unicode, special chars, optional fields |
| `NoteDaoTest` | 16 | Room DAO: upsert, expiry queries, soft delete, purge, ciphertext overwrite |
| `PanicWipeServiceTest` | 6 | Session closure, wipeAll/wipeDecoyOnly without throwing |
| `NulvexUiTest` | 31 | Compose UI: onboarding, setup PIN, unlock pad, vault list, panic button, error banner |

CI workflow (`.github/workflows/android-unit-tests.yml`) runs JVM tests only (`./gradlew test` + JaCoCo report + JaCoCo coverage gate) on every push/PR to `main`, `master`, and `develop`, and uploads HTML/XML coverage artifacts.  
`connectedAndroidTest` is not executed in GitHub Actions.

---

## Roadmap

- [x] Core test suite (crypto, vault, self-destruct, panic, DAO)
- [x] UI flow tests (Compose: onboarding, setup, unlock, vault, panic, error banner)
- [x] Firebase Gradle plugin + Analytics dependency
- [x] Play Billing base wiring (product IDs + BillingClient factory)
- [ ] Purchase flow integration (query, launch billing flow, entitlement persistence)
- [ ] Security whitepaper + crypto flow diagram
- [ ] Play Store listing + privacy policy
- [ ] Device matrix test (StrongBox / no StrongBox, API 26 / 30 / 33 / 34+)
- [ ] Zero-knowledge sync backend (Pro)
- [ ] Remote panic wipe — cross-device trigger (Pro)
- [x] Encrypted export / backup (local + remote foundation)
- [x] Keys Manager with OpenPGP + XChaCha import/export
- [ ] Translations workflow (Transifex + multi-language resources)

---

## License

Copyright (C) 2026 Velimir Majstorov and AndroidIRCx

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

See [LICENSE](LICENSE) for the full text.
