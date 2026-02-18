# Contributing to Nulvex

Thank you for your interest in contributing. Because Nulvex handles sensitive encrypted data, contributions are held to a high bar — especially anything touching cryptography, key handling, or security-critical paths.

## Before You Start

- Open an issue first for non-trivial changes so we can discuss the approach
- For security-sensitive changes, see [SECURITY.md](SECURITY.md) before opening anything public
- Check the open issues and roadmap in [README.md](README.md) to avoid duplicate work

## Development Setup

1. Clone the repository
2. Open in Android Studio (Ladybug or newer recommended)
3. Build: `./gradlew assembleDebug`
4. The app requires Android 8.0+ (API 26)

No external accounts or backend needed — fully offline.

## Pull Request Guidelines

- Keep PRs focused: one concern per PR
- Target the `main` branch
- Include a clear description of what changed and why
- If the change affects cryptography or security: explain the threat model impact
- All existing tests must pass; add tests for new security-critical code

## Cryptography Changes

Changes to any of the following require explicit maintainer approval before merging:

- `Argon2idKdf.kt` — KDF parameters or library
- `VaultKeyManager.kt` — key derivation, wrapping, Keystore usage
- `XChaCha20Poly1305NoteCrypto.kt` — encryption primitive
- `VaultAuthService.kt` — authentication logic
- `PanicWipeService.kt` — wipe logic
- Database schema or SQLCipher configuration

## Code Style

- Kotlin official style (`kotlin.code.style=official`)
- No new dependencies without discussion — every added library is an attack surface
- No logging of key material, plaintext content, or PINs

## License

By contributing you agree that your contributions are licensed under the [GNU General Public License v3.0](LICENSE).
