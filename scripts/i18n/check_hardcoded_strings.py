#!/usr/bin/env python3
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[2]
UI_DIR = ROOT / "app" / "src" / "main" / "java" / "com" / "androidircx" / "nulvex" / "ui"

# Heuristic: flag direct literals in Compose Text calls.
TEXT_PATTERNS = [
    re.compile(r"\bText\(\s*\"([^\"\\]*(?:\\.[^\"\\]*)*)\""),
    re.compile(r"\bText\(\s*text\s*=\s*\"([^\"\\]*(?:\\.[^\"\\]*)*)\""),
]

ALLOW_SUBSTRINGS = (
    "tx(",
    "stringResource(",
    "R.string.",
)


def is_probably_user_facing(value: str) -> bool:
    v = value.strip()
    if not v:
        return False
    if len(v) < 2:
        return False
    if "http://" in v or "https://" in v:
        return False
    return any(c.isalpha() for c in v)


def main() -> int:
    violations: list[str] = []

    for file in UI_DIR.rglob("*.kt"):
        content = file.read_text(encoding="utf-8", errors="ignore")
        lines = content.splitlines()

        for i, line in enumerate(lines, start=1):
            if "Text(" not in line:
                continue
            if any(token in line for token in ALLOW_SUBSTRINGS):
                continue

            for pattern in TEXT_PATTERNS:
                m = pattern.search(line)
                if not m:
                    continue
                literal = m.group(1)
                if is_probably_user_facing(literal):
                    rel = file.relative_to(ROOT)
                    violations.append(f"{rel}:{i}: hardcoded Text literal -> \"{literal}\"")

    if violations:
        print("Hardcoded user-facing Text literals detected:")
        for v in violations:
            print(f"- {v}")
        print("\nUse tx(\"...\") or stringResource(...) instead.")
        return 1

    print("No hardcoded Compose Text literals detected in ui package.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
