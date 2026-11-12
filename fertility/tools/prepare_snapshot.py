"""Create a code-review snapshot from two local Git checkouts, without Git history.

Usage: python tools/prepare_snapshot.py BACKEND_REPO BACKEND_REF AI_REPO AI_REF
This helper is not part of the application runtime. It never changes source repos.
"""

from __future__ import annotations

import os
import re
import subprocess
import sys
from pathlib import Path

DEST = Path(__file__).resolve().parents[1]
ENV = dict(os.environ, GIT_NO_LAZY_FETCH="1")
EXCLUDED_BACKEND_DOCS = {
    "docs/COMMUNITY_API_SPEC.md",
    "docs/FRONTEND_BACKEND_REPLY.md",
    "docs/fastapi_top_factors_padding.py",
}


def git(repo: Path, *args: str) -> bytes:
    return subprocess.check_output(["git", "-C", str(repo), *args], env=ENV)


def include(path: str, kind: str) -> bool:
    parts = Path(path).parts
    if path == "README.md" or ".DS_Store" in parts:
        return False
    if parts[0] in {".git", ".github", "build", ".gradle", "scripts"}:
        return False
    if Path(path).suffix.lower() in {".pkl", ".pickle", ".csv", ".parquet", ".db", ".log"}:
        return False
    if Path(path).name.startswith(".env"):
        return False
    return kind != "backend" or path not in EXCLUDED_BACKEND_DOCS


def snapshot(repo: Path, ref: str, kind: str) -> None:
    count = 0
    sanitized = 0
    for entry in git(repo, "ls-tree", "-rz", ref).split(b"\0"):
        if not entry:
            continue
        metadata, raw_path = entry.split(b"\t", 1)
        mode, obj_type, oid = metadata.decode().split()
        rel = raw_path.decode()
        if obj_type != "blob" or mode == "120000" or not include(rel, kind):
            continue
        target = DEST / kind / rel
        data = git(repo, "cat-file", "blob", oid)
        try:
            original = data.decode("utf-8")
        except UnicodeDecodeError:
            # The Gradle wrapper JAR is the sole retained binary build tool.
            if rel != "gradle/wrapper/gradle-wrapper.jar":
                continue
        else:
            def replace_endpoint(match: re.Match[str]) -> str:
                value = match.group()
                # Keep intentional loopback/connection-refused test endpoints.
                if re.match(r"https?://(?:127\.|0\.0\.0\.0)", value):
                    return value
                return "http://127.0.0.1:8080"

            text = re.sub(r"https?://(?:\d{1,3}\.){3}\d{1,3}(?::\d+)?", replace_endpoint, original)
            if rel.startswith("src/test/"):
                fixture_emails: dict[str, str] = {}

                def replace_fixture_email(match: re.Match[str]) -> str:
                    original_email = match.group()
                    if original_email not in fixture_emails:
                        fixture_emails[original_email] = f"fixture{len(fixture_emails) + 1}@example.invalid"
                    return fixture_emails[original_email]

                text = re.sub(r"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}", replace_fixture_email, text)
            sanitized += text != original
            data = text.encode("utf-8")
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(data)
        if mode == "100755":
            target.chmod(0o755)
        count += 1
    print(f"{kind}: {count} files, address placeholders updated in {sanitized} files")


if __name__ == "__main__":
    if len(sys.argv) != 5:
        raise SystemExit(__doc__)
    snapshot(Path(sys.argv[1]), sys.argv[2], "backend")
    snapshot(Path(sys.argv[3]), sys.argv[4], "ai")
