# NULVEX Backup and Restore Threat/Retention Policy

Last updated: 2026-02-27

## 1) Scope

Applies to:
- local encrypted backup files (`.nulvxbk`)
- encrypted note-share files (`.nulvex`)
- key-manager exports (`.nulvxkeys`)
- remote media backup references when Pro remote backup is used

## 2) Threat Model for Backup Flows

Primary risks:
- stolen backup file from device/shared storage
- accidental plaintext export
- replay of stale backup into active vault
- backup metadata leakage
- malicious/corrupted import payloads

Current mitigations:
- encrypted payload formats
- malformed payload rejection paths and tests
- import validation for attachments/base64 wrappers
- panic wipe and vault lock lifecycle controls

## 3) Retention Rules

Default retention guidance:
- keep only latest required local backup generations
- remove obsolete backup files from shared storage manually or via app cleanup UX
- purge stale backup registry entries when no longer valid

Recommended operational retention:
- personal user mode: 1-3 recent backups
- high-risk mode: keep minimal snapshots and rotate frequently

## 4) Restore Policy

- restore is explicit user action only
- merge/non-merge restore modes must be user-selected
- failed import should never partially apply malformed payloads
- restore should preserve vault profile isolation (`real` vs `decoy`)

## 5) Remote Media Notes

- remote server should be treated as untrusted ciphertext store
- remote identifiers/tokens must be protected as sensitive metadata
- expired/unused remote records should be deleted from local registry

## 6) Compliance/Process

Before release:
- verify import/export regression tests are green
- verify manifest file types and deep-link sanitization coverage
- verify backup policy text is present in user-facing docs/settings help

## 7) Future Enhancements

- optional backup expiry metadata and auto-prune UX
- signed backup manifests to improve tamper evidence
- stricter restore confirmation UX for destructive overwrite mode
