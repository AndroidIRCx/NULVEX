# NULVEX Crypto Flow Diagram

```mermaid
flowchart TD
    A[User PIN/Password] --> B[Argon2id]
    B --> C[Master Seed]
    C --> D[HKDF-SHA-256]
    D --> E[SQLCipher DB Key]
    D --> F[Note Encryption Key]

    G[Android Keystore/StrongBox] --> H[Wrapped Master Key Material]
    H --> I[BiometricPrompt Auth Gate]
    I --> J[Unlock Session]

    J --> K[XChaCha20-Poly1305 Note Encrypt/Decrypt]
    K --> L[Ciphertext in SQLCipher DB]

    M[Panic Wipe] --> N[Close Session]
    M --> O[Delete DB + Keystore artifacts + prefs]
```

Notes:
- Biometrics are used for authentication to unlock wrapped key material.
- Biometrics are not a standalone cryptographic secret source.
- Hybrid KEM module (X25519 + ML-KEM-768) is available for future sync envelopes.
