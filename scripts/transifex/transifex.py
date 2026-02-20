#!/usr/bin/env python3
import argparse
import json
import os
import pathlib
import re
import ssl
import sys
import time
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET

API_BASE = "https://rest.api.transifex.com"


def load_env_file(path: pathlib.Path) -> None:
    if not path.exists():
        raise RuntimeError(f"Missing env file: {path}")
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        os.environ[key.strip()] = value.strip().strip('"')


def api_request(method: str, url: str, token: str, body: dict | None = None, want_json: bool = True):
    headers = {"Authorization": f"Bearer {token}"}
    data = None
    if body is not None:
        headers["Content-Type"] = "application/vnd.api+json"
        data = json.dumps(body).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=60) as resp:
            content = resp.read()
            content_type = resp.headers.get("Content-Type", "")
    except ssl.SSLError:
        # Some Windows environments have broken cert chains in local Python stores.
        unverified = ssl._create_unverified_context()
        with urllib.request.urlopen(req, timeout=60, context=unverified) as resp:
            content = resp.read()
            content_type = resp.headers.get("Content-Type", "")
    if want_json:
        return json.loads(content.decode("utf-8"))
    return content.decode("utf-8"), content_type


def get_json(path: str, token: str):
    return api_request("GET", f"{API_BASE}{path}", token, want_json=True)


def post_json(path: str, token: str, body: dict):
    return api_request("POST", f"{API_BASE}{path}", token, body=body, want_json=True)


def get_text(path: str, token: str):
    return api_request("GET", f"{API_BASE}{path}", token, want_json=False)


def delete_resource(resource_id: str, token: str):
    return api_request("DELETE", f"{API_BASE}/resources/{resource_id}", token, want_json=False)


def resolve_context(token: str, project_name: str, org_slug: str | None):
    if org_slug:
        organization_id = f"o:{org_slug}"
    else:
        orgs = get_json("/organizations", token)
        data = orgs.get("data", [])
        if not data:
            raise RuntimeError("No organizations available for this token.")
        organization_id = data[0]["id"]

    q = urllib.parse.quote(organization_id, safe="")
    n = urllib.parse.quote(project_name, safe="")
    projects = get_json(f"/projects?filter[organization]={q}&filter[name]={n}", token)
    pdata = projects.get("data", [])
    if not pdata:
        raise RuntimeError(f"Project '{project_name}' not found in organization '{organization_id}'.")
    return organization_id, pdata[0]["id"]


def ensure_resource(token: str, project_id: str, resource_slug: str):
    q = urllib.parse.quote(project_id, safe="")
    resources = get_json(f"/resources?filter[project]={q}", token).get("data", [])
    for item in resources:
        if item.get("attributes", {}).get("slug") == resource_slug:
            return item["id"]

    payload = {
        "data": {
            "type": "resources",
            "attributes": {
                "name": resource_slug,
                "slug": resource_slug,
            },
            "relationships": {
                "project": {"data": {"type": "projects", "id": project_id}},
                "i18n_format": {"data": {"type": "i18n_formats", "id": "ANDROID"}},
            },
        }
    }
    created = post_json("/resources", token, payload)
    return created["data"]["id"]


def find_resource_id(token: str, project_id: str, resource_slug: str) -> str | None:
    q = urllib.parse.quote(project_id, safe="")
    resources = get_json(f"/resources?filter[project]={q}", token).get("data", [])
    for item in resources:
        if item.get("attributes", {}).get("slug") == resource_slug:
            return item["id"]
    return None


def poll_async_upload(token: str, upload_id: str):
    for _ in range(60):
        status_doc = get_json(f"/resource_strings_async_uploads/{upload_id}", token)
        status = status_doc["data"]["attributes"]["status"]
        if status == "succeeded":
            return
        if status == "failed":
            errors = status_doc["data"]["attributes"].get("errors", [])
            raise RuntimeError(f"Upload failed: {errors}")
        time.sleep(1)
    raise RuntimeError("Upload polling timed out.")


def merge_android_string_files(primary: pathlib.Path, extra: pathlib.Path | None) -> str:
    base_tree = ET.parse(primary)
    base_root = base_tree.getroot()
    if base_root.tag != "resources":
        raise RuntimeError(f"Invalid Android resources root in {primary}")

    seen_keys = set()
    for child in list(base_root):
        name = child.attrib.get("name")
        if name:
            seen_keys.add((child.tag, name))

    if extra and extra.exists():
        extra_tree = ET.parse(extra)
        extra_root = extra_tree.getroot()
        if extra_root.tag != "resources":
            raise RuntimeError(f"Invalid Android resources root in {extra}")
        for child in list(extra_root):
            name = child.attrib.get("name")
            if not name:
                continue
            key = (child.tag, name)
            if key in seen_keys:
                continue
            seen_keys.add(key)
            base_root.append(child)

    return ET.tostring(base_root, encoding="unicode")


def push_sources(root: pathlib.Path, token: str, project_name: str, org_slug: str | None, resource_slug: str):
    _, project_id = resolve_context(token, project_name, org_slug)
    resource_id = ensure_resource(token, project_id, resource_slug)
    source_file = root / "app/src/main/res/values/strings.xml"
    extracted_file = root / "app/src/main/res/values/strings_extracted.xml"
    if not source_file.exists():
        raise RuntimeError(f"Source strings file not found: {source_file}")
    content = merge_android_string_files(source_file, extracted_file)

    payload = {
        "data": {
            "type": "resource_strings_async_uploads",
            "attributes": {
                "content": content,
                "content_encoding": "text",
            },
            "relationships": {
                "resource": {
                    "data": {
                        "type": "resources",
                        "id": resource_id,
                    }
                }
            },
        }
    }
    created = post_json("/resource_strings_async_uploads", token, payload)
    upload_id = created["data"]["id"]
    poll_async_upload(token, upload_id)
    print(f"Pushed source strings to {resource_id}")


def reset_resource(token: str, project_name: str, org_slug: str | None, resource_slug: str):
    _, project_id = resolve_context(token, project_name, org_slug)
    resource_id = find_resource_id(token, project_id, resource_slug)
    if not resource_id:
        print(f"Resource '{resource_slug}' not found. Nothing to reset.")
        return
    delete_resource(resource_id, token)
    print(f"Deleted resource {resource_id}")


def android_qualifier(lang_code: str) -> str:
    raw = lang_code.strip()
    if "@" in raw:
        lang, script = raw.split("@", 1)
        mapped_script = {
            "latin": "Latn",
            "cyrl": "Cyrl",
        }.get(script.lower(), script[:1].upper() + script[1:].lower())
        return f"b+{lang.lower()}+{mapped_script}"

    normalized = raw.replace("_", "-")
    parts = [p for p in normalized.split("-") if p]
    if len(parts) == 1:
        return parts[0].lower()
    if len(parts) >= 2 and len(parts[1]) == 2:
        return f"{parts[0].lower()}-r{parts[1].upper()}"
    if len(parts) >= 2 and len(parts[1]) == 4:
        return f"b+{parts[0].lower()}+{parts[1][:1].upper() + parts[1][1:].lower()}"
    return parts[0].lower()


def fetch_download_content(token: str, download_id: str) -> str:
    for _ in range(90):
        text, content_type = get_text(f"/resource_translations_async_downloads/{download_id}", token)
        ct = (content_type or "").lower()
        if "application/vnd.api+json" in ct or text.lstrip().startswith("{"):
            doc = json.loads(text)
            status = doc.get("data", {}).get("attributes", {}).get("status")
            if status == "failed":
                errors = doc.get("data", {}).get("attributes", {}).get("errors", [])
                raise RuntimeError(f"Translation download failed: {errors}")
            time.sleep(1)
            continue
        return text
    raise RuntimeError("Translation download polling timed out.")


def has_translatable_entries(xml_content: str) -> bool:
    # Keep file only when it has at least one translatable entry.
    return bool(
        re.search(r"<string\s+name=", xml_content)
        or re.search(r"<plurals\s+name=", xml_content)
        or re.search(r"<string-array\s+name=", xml_content)
    )


def pull_translations(root: pathlib.Path, token: str, project_name: str, org_slug: str | None, resource_slug: str):
    _, project_id = resolve_context(token, project_name, org_slug)
    resource_id = ensure_resource(token, project_id, resource_slug)

    q = urllib.parse.quote(project_id, safe="")
    stats = get_json(f"/resource_language_stats?filter[project]={q}", token).get("data", [])

    pulled = 0
    skipped_empty = 0
    for item in stats:
        rel = item.get("relationships", {})
        resource = rel.get("resource", {}).get("data", {}).get("id")
        language = rel.get("language", {}).get("data", {}).get("id", "")
        if resource != resource_id:
            continue
        if language == "l:en":
            continue

        payload = {
            "data": {
                "type": "resource_translations_async_downloads",
                "relationships": {
                    "resource": {"data": {"type": "resources", "id": resource_id}},
                    "language": {"data": {"type": "languages", "id": language}},
                },
            }
        }
        created = post_json("/resource_translations_async_downloads", token, payload)
        download_id = created["data"]["id"]
        xml_content = fetch_download_content(token, download_id)

        lang_code = language.replace("l:", "")
        qualifier = android_qualifier(lang_code)
        out_dir = root / f"app/src/main/res/values-{qualifier}"
        out_file = out_dir / "strings.xml"

        # If translation file is empty, do not keep localized file.
        # Android will then fallback to default English resources.
        if not has_translatable_entries(xml_content):
            skipped_empty += 1
            if out_file.exists():
                out_file.unlink()
            # Remove empty values-xx dir created by previous pulls.
            if out_dir.exists():
                try:
                    out_dir.rmdir()
                except OSError:
                    pass
            continue

        out_dir.mkdir(parents=True, exist_ok=True)
        out_file.write_text(xml_content, encoding="utf-8")
        pulled += 1

    print(
        f"Pulled {pulled} translation file(s) for resource {resource_id} "
        f"(skipped empty: {skipped_empty})"
    )


def main() -> int:
    parser = argparse.ArgumentParser(description="Transifex push/pull for Android strings.xml")
    parser.add_argument("command", choices=["push", "pull", "reset"])
    parser.add_argument("--root", default=".")
    args = parser.parse_args()

    root = pathlib.Path(args.root).resolve()
    load_env_file(root / "secrets/transifex.env")

    token = os.environ.get("TRANSIFEX_API_TOKEN") or os.environ.get("TRANSIFEX_TOKEN")
    if not token:
        raise RuntimeError("Missing TRANSIFEX_API_TOKEN or TRANSIFEX_TOKEN in secrets/transifex.env")

    project_name = os.environ.get("TRANSIFEX_PROJECT_NAME", "NULVEX")
    resource_slug = os.environ.get("TRANSIFEX_RESOURCE_SLUG", "android-strings")
    org_slug = os.environ.get("TRANSIFEX_ORG_SLUG")

    if args.command == "push":
        push_sources(root, token, project_name, org_slug, resource_slug)
    elif args.command == "pull":
        pull_translations(root, token, project_name, org_slug, resource_slug)
    else:
        reset_resource(token, project_name, org_slug, resource_slug)
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(1)
