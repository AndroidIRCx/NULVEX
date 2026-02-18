# Nulvex

**Offline-first encrypted notes vault for Android.**
No accounts. No telemetry. No plaintext on disk.

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

### UX
- **Secure PIN pad** — custom circular numpad, no system keyboard; dot indicator, haptic feedback
- **Light / Dark / System theme**
- **Search** — full-text search across decrypted note content

---

## Cryptography

| Layer | Primitive |
|---|---|
| KDF | Argon2id (256 MiB / 3 iter / p=2) |
| Subkey derivation | HKDF-SHA-256 |
| Note encryption | XChaCha20-Poly1305 (via Google Tink) |
| Keystore wrapping | AES-256-GCM (Android Keystore / StrongBox) |
| Biometric wrapping | AES-256-GCM cipher-based BiometricPrompt |
| Hybrid KEM (planned) | X25519 + ML-KEM-768 for future sync |

Key hierarchy:

```
PIN / password
    └─ Argon2id ──► Master Seed
                        └─ HKDF ──► DB key (SQLCipher)
                                    Note encryption key(s)
                                    Sync envelope key(s) [planned]
```

Biometrics authenticate the user but do not derive cryptographic keys — the master key is stored encrypted by a Keystore-backed AES cipher locked to biometric authentication.

---

## Requirements

- Android 8.0+ (API 26)
- Kotlin 2.2.10
- AGP 9.0.1 / Gradle 9.2.1

---

## Build

```bash
# Debug
./gradlew assembleDebug

# Release AAB (signs if secrets/keystore.properties is present)
./gradlew bundleRelease
```

Release signing config is loaded from `secrets/keystore.properties` (gitignored):

```properties
storeFile=../secrets/nulvex.keystore
storePassword=...
keyAlias=...
keyPassword=...
```

---

## Release

Releases are automated with Fastlane. One command builds, bumps the version, commits, pushes and uploads to Google Play Closed testing:

```bash
fastlane closed
```

What it does:
1. `bundleRelease` — builds the AAB and auto-increments `versionCode` + `versionName`
2. `git commit` + `push` — commits `version.properties` and pushes to `main`
3. `upload_to_play_store` — uploads AAB + R8 mapping to the **alpha** track

See [`fastlane/README.md`](fastlane/README.md) for all available lanes.

Google Play API credentials are loaded from `secrets/androidircx-*.json` (gitignored).

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
│   ├── VaultAuthService.kt   # PIN verify, decoy, attempt limiting
│   ├── PanicWipeService.kt   # Full wipe + decoy-only wipe
│   ├── BiometricKeyStore.kt  # Encrypt/decrypt master key via BiometricPrompt
│   └── AppPreferences.kt     # Lock timeout, lockout state, theme
├── ui/
│   ├── MainScreen.kt         # All Compose screens
│   ├── MainViewModel.kt      # Single UiState, all app logic
│   └── theme/
└── work/
    └── SelfDestructWorker.kt # WorkManager: sweep expired notes
```

---

## Threat Model (summary)

**Protected against:**
- Physical access to device at rest
- Full filesystem access (rooted / adb backup)
- Network observer during sync (planned zero-knowledge backend)
- Coercion / forced unlock (decoy vault + panic wipe)
- Device theft without unlock credentials

**Out of scope (v1):**
- Compromise of device while unlocked and running
- Full-device malware with memory access
- Hardware side-channel attacks

---

## Roadmap

- [ ] Instrumented test suite (Argon2, VaultAuthService, NoteRepository, SelfDestructService)
- [ ] Security whitepaper + crypto flow diagram
- [ ] Play Store listing + privacy policy
- [ ] Device matrix test (StrongBox / no StrongBox, API 26 / 30 / 33 / 34+)
- [ ] Zero-knowledge sync backend (Pro)
- [ ] Remote panic wipe — cross-device trigger (Pro)
- [ ] Encrypted export / backup (Pro)

---

## License

Copyright (C) 2026 Velimir Majstorov and AndroidIRCx

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

See [LICENSE](LICENSE) for the full text.
