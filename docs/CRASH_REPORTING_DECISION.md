# Crash Reporting Decision (v1)

Last updated: 2026-02-27

Decision:
- Keep Firebase Crashlytics disabled for current release line.

Rationale:
- NULVEX prioritizes strict privacy posture.
- Current telemetry already includes Firebase Analytics and should remain minimal.
- Crash payloads may accidentally include sensitive context if not heavily curated.

Revisit criteria:
- add explicit privacy review for crash payload fields
- verify redaction strategy and PII/sensitive-content filters
- provide opt-in or transparent disclosure path if enabled

Implementation status:
- Crashlytics plugin/dependencies are not enabled in current Gradle config.
- Decision can be revisited after release stabilization and policy/legal review.
