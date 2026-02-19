# Security Policy

## Supported Versions

| Version | Supported |
|---|---|
| Latest on `main` | ✅ |
| Older releases | ❌ |

## Reporting a Vulnerability

**Do not open a public GitHub issue for security vulnerabilities.**

Report vulnerabilities privately via GitHub's built-in security advisory system:

1. Go to the **Security** tab of this repository
2. Click **"Report a vulnerability"**
3. Fill in the details

We aim to respond within **72 hours** and to release a patch within **14 days** for confirmed critical issues.

Please include:
- Description of the vulnerability
- Steps to reproduce
- Affected versions
- Potential impact

## Scope

In scope:
- Cryptographic implementation flaws (Argon2id, XChaCha20-Poly1305, HKDF, key derivation)
- Authentication bypass (PIN, biometric, decoy vault)
- Data leakage (plaintext in logs, shared storage, clipboard)
- Panic wipe / self-destruct failures
- SQLCipher key handling issues

Out of scope:
- Vulnerabilities requiring root / physical device access while unlocked
- Issues in third-party libraries (report upstream)
- Denial of service against the local device

## Cryptographic Design

The threat model, key hierarchy, and crypto flow are documented internally and shared with confirmed contributors under NDA.
