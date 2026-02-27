# NULVEX Sync Protocol Spec (Draft v0.1)

Last updated: 2026-02-27
Status: Draft for implementation kickoff

## 1) Goals

- Optional Pro feature
- Zero-knowledge transport and storage
- Offline-first with reliable replay
- Deterministic conflict policy

## 2) Entities

- `device_id`: stable per installed client
- `vault_profile`: `real` or `decoy` scope isolation
- `note_id`: UUID
- `revision_id`: monotonic logical revision marker per note
- `op_id`: unique operation id (idempotency key)

## 3) Envelope

Each sync operation sends encrypted payload only.

```json
{
  "v": 1,
  "device_id": "...",
  "profile": "real",
  "op_id": "uuid",
  "op_type": "upsert|delete|metadata",
  "entity": "note",
  "entity_id": "note_uuid",
  "client_ts": 1730000000000,
  "base_revision": "rev_17",
  "cipher": {
    "alg": "xchacha20poly1305",
    "nonce": "base64",
    "ct": "base64"
  },
  "aad": {
    "schema": 1,
    "content_type": "application/x-nulvex-note"
  }
}
```

Server requirements:
- store envelope as opaque blob
- no plaintext note fields
- reject malformed envelope versions

## 4) Sync Cursor Model

Client keeps:
- `last_ack_cursor`
- `outbox` queue with retry metadata
- `pending_conflicts`

Server returns:
- ordered change feed with cursor token
- per-operation ack states

## 5) Conflict Strategy

Default strategy (v1): `LWW by logical revision + client_ts fallback`

Rules:
1. if incoming `base_revision` is behind latest known revision, mark conflict
2. if conflict and auto-resolve enabled: keep higher revision
3. if equal revision: compare `client_ts`
4. persist conflict event for optional UI resolution later

Future option:
- versioned conflict branches with manual merge UI

## 6) Idempotency and Retry

- `op_id` must be unique and deduplicated server-side
- retries are safe; duplicate `op_id` returns prior ack
- exponential backoff on network/server failures

## 7) Security Requirements

- auth token bound to account + device
- optional device registration handshake
- strict profile isolation (`real` and `decoy` must never mix)
- request signing optional phase 2

## 8) API Surface (Proposed)

- `POST /sync/v1/devices/register`
- `POST /sync/v1/ops/push`
- `GET /sync/v1/ops/pull?cursor=...`
- `POST /sync/v1/ops/ack`
- `POST /sync/v1/panic/remote` (future gated)

## 9) Client Engine Loop

1. Load outbox and cursor
2. Push pending ops batch
3. Pull remote ops batch
4. Apply with conflict rules
5. Persist new cursor + ack
6. schedule retry if partial failure

## 10) Implementation Milestones

1. Implement local sync state tables (outbox/cursor/conflicts)
2. Add envelope serializer/deserializer tests
3. Build Laravel storage endpoints and op dedupe
4. Build Android sync worker integration
5. Add integration tests for replay/conflict/offline recovery
