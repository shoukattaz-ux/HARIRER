#!/usr/bin/env python3
"""
Publish a HairLook APK update to Cloudflare R2 for installs that did NOT come from Google Play.
(Google Play installs update through Play itself; never upload Play builds here.)

Credentials are read from environment variables only. Never put them in the app or in git:
  R2_ACCESS_KEY_ID       R2 API token access key ID
  R2_SECRET_ACCESS_KEY   R2 API token secret access key
  R2_ENDPOINT            https://<ACCOUNT_ID>.r2.cloudflarestorage.com  (S3 API endpoint, private)
  R2_PUBLIC_BASE_URL     public URL of the bucket, e.g. https://updates.example.com or https://pub-xxxx.r2.dev
  R2_BUCKET              bucket name (default: hairstyleapp)

Usage:
  pip install boto3
  python scripts/publish_update.py app/build/outputs/apk/release/app-release.apk \
      --version-code 4 --version-name 1.3 --notes "New AI looks" [--min-supported 3] [--dry-run]

The APK is uploaded first and update.json last, so the manifest never points at a missing file.
"""
import argparse
import hashlib
import json
import os
import sys
from urllib.parse import urlparse

MANIFEST_KEY = "update.json"


def fail(message: str) -> None:
    sys.exit(f"error: {message}")


def sha256_of(path: str) -> str:
    digest = hashlib.sha256()
    with open(path, "rb") as handle:
        for chunk in iter(lambda: handle.read(1 << 20), b""):
            digest.update(chunk)
    return digest.hexdigest()


def https_url(value: str, name: str) -> str:
    parsed = urlparse(value or "")
    if parsed.scheme != "https" or not parsed.netloc:
        fail(f"{name} must be an https:// URL")
    return value.rstrip("/")


def build_manifest(args, public_base: str, apk_key: str, apk_sha256: str) -> dict:
    return {
        "versionCode": args.version_code,
        "versionName": args.version_name,
        "apkUrl": f"{public_base}/{apk_key}",
        "minSupportedVersionCode": args.min_supported,
        "releaseNotes": args.notes,
        "sha256": apk_sha256,
    }


def parse_args(argv):
    parser = argparse.ArgumentParser(description="Publish a HairLook APK update to Cloudflare R2.")
    parser.add_argument("apk", help="path to the signed release APK")
    parser.add_argument("--version-code", type=int, required=True, help="versionCode of the APK")
    parser.add_argument("--version-name", required=True, help="versionName of the APK, e.g. 1.3")
    parser.add_argument("--min-supported", type=int, default=0,
                        help="installs below this versionCode must update (default 0 = optional update)")
    parser.add_argument("--notes", default="", help="release notes shown in the update dialog")
    parser.add_argument("--bucket", default=os.environ.get("R2_BUCKET", "hairstyleapp"))
    parser.add_argument("--force", action="store_true", help="publish even if not newer than the live version")
    parser.add_argument("--dry-run", action="store_true", help="print the manifest without uploading")
    return parser.parse_args(argv)


def main(argv=None) -> None:
    args = parse_args(argv)
    if not os.path.isfile(args.apk) or not args.apk.endswith(".apk"):
        fail(f"APK not found: {args.apk}")
    if args.version_code <= 0:
        fail("--version-code must be positive")
    if args.min_supported > args.version_code:
        fail("--min-supported cannot be higher than --version-code")

    public_base = https_url(os.environ.get("R2_PUBLIC_BASE_URL", ""), "R2_PUBLIC_BASE_URL")
    if "r2.cloudflarestorage.com" in public_base:
        fail("R2_PUBLIC_BASE_URL must be the bucket's PUBLIC URL (custom domain or r2.dev), "
             "not the private S3 API endpoint")

    apk_key = f"releases/hairlook-{args.version_name}-{args.version_code}.apk"
    manifest = build_manifest(args, public_base, apk_key, sha256_of(args.apk))
    manifest_json = json.dumps(manifest, indent=2)

    if args.dry_run:
        print(f"[dry run] would upload {args.apk} -> s3://{args.bucket}/{apk_key}")
        print(f"[dry run] would upload {MANIFEST_KEY}:\n{manifest_json}")
        return

    access_key = os.environ.get("R2_ACCESS_KEY_ID")
    secret_key = os.environ.get("R2_SECRET_ACCESS_KEY")
    if not access_key or not secret_key:
        fail("set R2_ACCESS_KEY_ID and R2_SECRET_ACCESS_KEY in your environment")
    endpoint = https_url(os.environ.get("R2_ENDPOINT", ""), "R2_ENDPOINT")
    # Accept the endpoint copied with the bucket path appended (…cloudflarestorage.com/<bucket>)
    if endpoint.endswith("/" + args.bucket):
        endpoint = endpoint[: -len(args.bucket) - 1]

    try:
        import boto3
        from botocore.config import Config
        from botocore.exceptions import ClientError
    except ImportError:
        fail("boto3 is required: pip install boto3")

    s3 = boto3.client(
        "s3",
        endpoint_url=endpoint,
        aws_access_key_id=access_key,
        aws_secret_access_key=secret_key,
        region_name="auto",
        config=Config(signature_version="s3v4"),
    )

    try:
        live = json.loads(s3.get_object(Bucket=args.bucket, Key=MANIFEST_KEY)["Body"].read())
        live_code = int(live.get("versionCode", 0))
    except ClientError:
        live_code = 0
    if live_code >= args.version_code and not args.force:
        fail(f"live update.json already has versionCode {live_code}; bump --version-code or use --force")

    print(f"Uploading {args.apk} -> s3://{args.bucket}/{apk_key}")
    s3.upload_file(
        args.apk, args.bucket, apk_key,
        ExtraArgs={
            "ContentType": "application/vnd.android.package-archive",
            "CacheControl": "public, max-age=31536000, immutable",
        },
    )
    print(f"Uploading {MANIFEST_KEY}")
    s3.put_object(
        Bucket=args.bucket,
        Key=MANIFEST_KEY,
        Body=manifest_json.encode("utf-8"),
        ContentType="application/json",
        CacheControl="no-cache",
    )
    print(f"Published {args.version_name} ({args.version_code}). Manifest: {public_base}/{MANIFEST_KEY}")


if __name__ == "__main__":
    main()
