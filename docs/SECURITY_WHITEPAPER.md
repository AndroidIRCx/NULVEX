# NULVEX Security Whitepaper (v1)

Last updated: 2026-02-27

## 1. Scope

NULVEX is an offline-first Android notes vault focused on confidentiality at rest, coercion resistance, and secure local lifecycle.

## 2. Threat Model

In scope:
- Lost/stolen device at rest
- Offline brute-force against user PIN/password
- Filesystem extraction attempts
- Metadata minimization concerns
- Coercion scenarios (decoy profile)

Out of scope for v1:
- Full compromise of unlocked runtime memory by advanced malware
- Hardware side-channel attacks against TEE/StrongBox

## 3. Cryptographic Design

- KDF: Argon2id
- Key expansion: HKDF-SHA-256
- Note payload encryption: XChaCha20-Poly1305
- SQLCipher DB encryption key: derived at unlock, never stored plaintext
- Keystore wrapping: Android Keystore (StrongBox when available)
- Biometrics: authentication gate for keystore decryption only
- Hybrid KEM readiness: X25519 + ML-KEM-768 support component present

## 4. Key Hierarchy

`PIN/password -> Argon2id master seed -> HKDF subkeys -> DB key + note key(s)`

Properties:
- key material is ephemeral in active session
- lock/panic paths clear session state and sensitive buffers where applicable

## 5. Data Lifecycle Controls

- Read-once notes (burn after first read)
- Time-based expiration sweeps
- Periodic self-destruct worker + unlock-time sweep
- Panic wipe for full vault reset
- Decoy vault isolation from real vault

## 6. Storage and Sync Posture

- Primary mode: fully offline
- Local backup/export formats are encrypted
- Remote media backup stores encrypted payloads only
- Full multi-device sync protocol remains post-launch work

## 7. Security Hardening Controls

- `FLAG_SECURE` to reduce screenshot/recents leakage
- PIN wrong-attempt lockout windows
- reminder/deep-link/input sanitization tests for abuse scenarios

## 8. Verification Status

Current verified pipelines include JVM and instrumented test suites, including targeted tests for:
- panic wipe
- auth/decoy resolution
- malformed import payload rejection
- deep-link media ID sanitization

## 9. Residual Risks and Planned Work

- Complete release device matrix evidence
- Publish formal sync protocol with conflict model
- Continue dependency vulnerability triage and patch cadence
