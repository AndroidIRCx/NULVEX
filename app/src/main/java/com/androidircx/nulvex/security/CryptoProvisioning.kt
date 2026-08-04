package com.androidircx.nulvex.security

/**
 * Process-wide lock guarding lazy creation of persisted key material:
 * Argon/DB salts, keystore-wrapped secrets, and AndroidKeystore key generation.
 *
 * Every get-or-create path that generates a random salt/secret or generates an
 * AndroidKeystore key must run under this single lock. Without it, two concurrent
 * callers (e.g. biometric enrollment on the main thread vs. a PIN unlock on
 * Dispatchers.IO) can both observe "no material yet", each generate a different
 * random value, and last-write-wins into SharedPreferences — leaving the
 * fingerprint-wrapped master key inconsistent with the key the DB was created
 * with, which permanently breaks unlock.
 *
 * A single global lock is intentional: these operations are rare (setup/unlock/
 * enrollment) so contention is negligible, and one lock removes any lock-ordering
 * or deadlock risk between the prefs and keystore layers (which are used together
 * inside a single provisioning call).
 */
internal object CryptoProvisioning {
    val lock = Any()
}
