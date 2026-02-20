#!/usr/bin/env python3
import hashlib
import html
import pathlib
import re

ROOT = pathlib.Path(__file__).resolve().parents[2]
SRC_ROOT = ROOT / "app/src/main/java/com/androidircx/nulvex"
OUT_FILE = ROOT / "app/src/main/res/values/strings_extracted.xml"
INCLUDE_GLOB = "**/*.kt"
PATTERNS = [
    re.compile(r'tx\("([^"\\]*(?:\\.[^"\\]*)*)"\)'),
    re.compile(r'showError\("([^"\\]*(?:\\.[^"\\]*)*)"\)'),
    re.compile(r'setBackupStatus\("([^"\\]*(?:\\.[^"\\]*)*)"\)'),
    re.compile(r'error\s*=\s*"([^"\\]*(?:\\.[^"\\]*)*)"'),
]
LITERAL_RE = re.compile(r'"([^"\\]*(?:\\.[^"\\]*)*)"')
LITERAL_FALLBACK_FILES = {
    "MainViewModel.kt",
    "MainActivity.kt",
    "PlayBillingCoordinator.kt",
}


def unescape_kotlin_string(s: str) -> str:
    return bytes(s, "utf-8").decode("unicode_escape")


def looks_user_facing(text: str) -> bool:
    t = text.strip()
    if not t:
        return False
    if len(t) < 2:
        return False
    if "http://" in t or "https://" in t:
        return False
    if "application/" in t:
        return False
    if t in {"*/*", "qr", "nfc", "system", "en", "sr"}:
        return False
    if "com.androidircx" in t or ".fileprovider" in t:
        return False
    if re.fullmatch(r"[A-Za-z0-9_./:+-]+", t) and t.islower():
        return False
    return any(c.isalpha() for c in t)


def key_for(text: str) -> str:
    digest = hashlib.sha1(text.encode("utf-8")).hexdigest()[:10]
    return f"tx_auto_{digest}"


def main() -> None:
    found: dict[str, set[str]] = {}

    for file_path in SRC_ROOT.glob(INCLUDE_GLOB):
        content = file_path.read_text(encoding="utf-8", errors="ignore")
        for pattern in PATTERNS:
            for m in pattern.finditer(content):
                raw = m.group(1)
                text = unescape_kotlin_string(raw).strip()
                if not looks_user_facing(text):
                    continue
                found.setdefault(text, set()).add(str(file_path.relative_to(ROOT)))

        if file_path.name in LITERAL_FALLBACK_FILES:
            for m in LITERAL_RE.finditer(content):
                raw = m.group(1)
                text = unescape_kotlin_string(raw).strip()
                if not looks_user_facing(text):
                    continue
                found.setdefault(text, set()).add(str(file_path.relative_to(ROOT)))

    lines = ["<resources>"]
    for text in sorted(found.keys()):
        key = key_for(text)
        refs = ", ".join(sorted(found[text]))
        lines.append(f"    <!-- auto-extracted from: {refs} -->")
        lines.append(f"    <string name=\"{key}\">{html.escape(text)}</string>")
    lines.append("</resources>")

    OUT_FILE.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote {OUT_FILE} with {len(found)} extracted strings")


if __name__ == "__main__":
    main()
