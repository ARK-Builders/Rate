#!/usr/bin/env python3
"""
update_crypto_data.py

1. Fetches top N cryptocurrencies from CoinGecko API
2. Saves market rates (price, cap, rank) to crypto-rates.json
3. Fetches fiat rates from OpenExchangeRates and saves to fiat-rates.json
4. Optionally downloads icons for iOS or Android into platform-specific folders
"""

import os
import sys
import json
import shutil
import time
import argparse
import re
import urllib.request
import urllib.error

# ── Configuration ─────────────────────────────────────────────────────────────

PROJECT_ROOT = os.environ.get(
    "CI_PROJECT_ROOT",
    os.path.dirname(os.path.abspath(__file__))
)
OPENEXCHANGERATES_API_KEY = os.environ.get("OPENEXCHANGERATES_API_KEY", "")
IOS_ICONS_DIR = os.path.join(PROJECT_ROOT, "Assets.xcassets", "cryptoicons")
IOS_RATES_DIR = os.path.join(PROJECT_ROOT, "Resources")
ANDROID_ICONS_DIR = os.path.join(PROJECT_ROOT, "cryptoicons", "src", "main", "res", "drawable")
ANDROID_RATES_DIR = os.path.join(PROJECT_ROOT, "core", "data", "src", "main", "assets")
CRYPTO_RATES_FILENAME = "crypto-rates.json"
FIAT_RATES_FILENAME = "fiat-rates.json"

# ── How many coins to download ────────────────────────────────────────────────
ICON_COUNT = 200          # Top 200 as requested

# ── CoinGecko safe delays ─────────────────────────────────────────────────────
COINGECKO_LIST_DELAY   = 2.0
DOWNLOAD_DELAY         = 0.3
MAX_RETRIES            = 5
BACKOFF_BASE           = 2

COINGECKO_MARKETS_URL  = (
    "https://api.coingecko.com/api/v3/coins/markets"
    "?vs_currency=usd&order=market_cap_desc"
    "&sparkline=false&per_page={per_page}&page={page}"
)
PER_PAGE               = 250

OPENEXCHANGERATES_URL  = "https://openexchangerates.org/api/latest.json?app_id={app_id}"

# ── Helpers ───────────────────────────────────────────────────────────────────

def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Update crypto and fiat rates for iOS or Android."
    )
    parser.add_argument(
        "--platform",
        choices=("ios", "android"),
        required=True,
        help="Target platform to update paths for.",
    )
    parser.add_argument(
        "--download-icons",
        action="store_true",
        help="Download crypto icons for the selected platform.",
    )
    return parser.parse_args()


def get_platform_paths(platform: str) -> tuple[str, str, str]:
    if platform == "ios":
        return PROJECT_ROOT, IOS_ICONS_DIR, IOS_RATES_DIR

    return PROJECT_ROOT, ANDROID_ICONS_DIR, ANDROID_RATES_DIR


def clean_directory(path: str) -> None:
    if os.path.exists(path):
        shutil.rmtree(path)
        print(f"  🗑  Cleaned: {path}")
    os.makedirs(path, exist_ok=True)
    print(f"  📁 Created: {path}")


def fetch_with_retry(url: str, label: str = "") -> bytes:
    headers = {"User-Agent": "UpdateDataScript/1.0"}
    for attempt in range(1, MAX_RETRIES + 1):
        try:
            req = urllib.request.Request(url, headers=headers)
            with urllib.request.urlopen(req, timeout=20) as resp:
                return resp.read()
        except urllib.error.HTTPError as e:
            if e.code == 429:
                wait = BACKOFF_BASE ** attempt
                print(f"  ⏳ Rate limited (429){' on ' + label if label else ''}. "
                      f"Waiting {wait}s … (attempt {attempt}/{MAX_RETRIES})")
                time.sleep(wait)
            elif attempt < MAX_RETRIES:
                wait = BACKOFF_BASE ** attempt
                print(f"  ⚠️  HTTP {e.code} {e.reason}{' on ' + label if label else ''}. "
                      f"Retrying in {wait}s …")
                time.sleep(wait)
            else:
                raise
        except Exception as e:
            if attempt < MAX_RETRIES:
                wait = BACKOFF_BASE ** attempt
                print(f"  ⚠️  Error{' on ' + label if label else ''}: {e}. "
                      f"Retrying in {wait}s …")
                time.sleep(wait)
            else:
                raise
    raise RuntimeError(f"Failed after {MAX_RETRIES} attempts: {url}")


def fetch_coin_list(total: int | None) -> list[dict]:
    coins: list[dict] = []
    page = 1
    while True:
        remaining = None if total is None else (total - len(coins))
        if remaining is not None and remaining <= 0:
            break
        per_page = PER_PAGE if remaining is None else min(PER_PAGE, remaining)
        url = COINGECKO_MARKETS_URL.format(per_page=per_page, page=page)
        print(f"  📄 Fetching page {page} ({per_page} coins/page) …", end=" ", flush=True)
        try:
            raw = fetch_with_retry(url, label=f"page {page}")
        except Exception as e:
            print(f"\n❌  Failed to fetch page {page}: {e}")
            sys.exit(1)
        batch: list = json.loads(raw.decode())
        if not batch:
            print("empty — done paginating.")
            break
        coins.extend(batch)
        print(f"got {len(batch)} coins (total so far: {len(coins)})")
        if len(batch) < per_page:
            break
        page += 1
        if page > 1:
            time.sleep(COINGECKO_LIST_DELAY)
    if total is not None:
        coins = coins[:total]
    return coins


def create_ios_imageset(parent_dir: str, asset_name: str, image_bytes: bytes) -> None:
    imageset_dir = os.path.join(parent_dir, f"{asset_name}.imageset")
    os.makedirs(imageset_dir, exist_ok=True)
    image_filename = f"{asset_name}.png"
    with open(os.path.join(imageset_dir, image_filename), "wb") as f:
        f.write(image_bytes)
    contents = {
        "images": [
            {"filename": image_filename, "idiom": "universal", "scale": "1x"},
            {"idiom": "universal", "scale": "2x"},
            {"idiom": "universal", "scale": "3x"}
        ],
        "info": {"author": "xcode", "version": 1}
    }
    with open(os.path.join(imageset_dir, "Contents.json"), "w") as f:
        json.dump(contents, f, indent=2)


def create_android_drawable(parent_dir: str, asset_name: str, image_bytes: bytes) -> None:
    image_filename = f"{asset_name}.png"
    with open(os.path.join(parent_dir, image_filename), "wb") as f:
        f.write(image_bytes)


def android_resource_name(symbol: str) -> str:
    resource_name = re.sub(r"[^a-z0-9_]", "_", symbol.lower())
    resource_name = re.sub(r"_+", "_", resource_name).strip("_")
    if not resource_name:
        return "coin_unknown"
    if not resource_name[0].isalpha():
        return f"coin_{resource_name}"
    return resource_name


def fetch_fiat_rates(rates_dir: str) -> None:
    if not OPENEXCHANGERATES_API_KEY:
        print("  ⚠️  OPENEXCHANGERATES_API_KEY env variable not set — skipping fiat rates.")
        return

    print(f"\n[2/5] Updating {FIAT_RATES_FILENAME} …")
    os.makedirs(rates_dir, exist_ok=True)
    fiat_file_path = os.path.join(rates_dir, FIAT_RATES_FILENAME)

    if os.path.exists(fiat_file_path):
        os.remove(fiat_file_path)
        print(f"  🗑  Deleted old {FIAT_RATES_FILENAME}")

    url = OPENEXCHANGERATES_URL.format(app_id=OPENEXCHANGERATES_API_KEY)
    try:
        raw = fetch_with_retry(url, label="OpenExchangeRates")
    except Exception as e:
        print(f"  ❌ Failed to fetch fiat rates: {e}")
        sys.exit(1)

    fiat_data = json.loads(raw.decode())

    with open(fiat_file_path, "w") as f:
        json.dump(fiat_data, f, indent=2)
    print(f"  ✅ Saved fiat rates to {fiat_file_path}")


def download_ios_icons(coins: list[dict], icons_dir: str) -> int:
    print(f"\n[4/5] Downloading icons …")
    tmp_dir = icons_dir + ".tmp"
    clean_directory(tmp_dir)

    downloaded = 0
    for i, coin in enumerate(coins, start=1):
        ticker    = coin.get("symbol", "").upper()
        image_url = coin.get("image", "")
        progress  = f"[{i}/{len(coins)}]"

        if not image_url:
            continue

        try:
            image_data = fetch_with_retry(image_url, label=ticker)
            create_ios_imageset(tmp_dir, ticker, image_data)
            downloaded += 1
            print(f"  {progress} ✅ {ticker:<10}")
        except Exception as e:
            print(f"  {progress} ❌ {ticker:<10} → {e}")

        if i < len(coins):
            time.sleep(DOWNLOAD_DELAY)

    print("\n[5/5] Finalizing Asset Catalog …")
    clean_directory(icons_dir)

    # Re-add the main Contents.json for the cryptoicons folder
    with open(os.path.join(icons_dir, "Contents.json"), "w") as f:
        json.dump({"info": {"author": "xcode", "version": 1}}, f, indent=2)

    for item in os.listdir(tmp_dir):
        if item.endswith(".imageset"):
            shutil.move(os.path.join(tmp_dir, item), os.path.join(icons_dir, item))

    shutil.rmtree(tmp_dir, ignore_errors=True)
    print(f"  ✅ Icons installed to: {icons_dir}")
    return downloaded


def download_android_icons(coins: list[dict], icons_dir: str) -> int:
    print(f"\n[4/5] Downloading icons …")
    clean_directory(icons_dir)

    downloaded = 0
    for i, coin in enumerate(coins, start=1):
        ticker = coin.get("symbol", "")
        resource_name = android_resource_name(ticker)
        image_url = coin.get("image", "")
        progress = f"[{i}/{len(coins)}]"

        if not image_url:
            continue

        try:
            image_data = fetch_with_retry(image_url, label=resource_name)
            create_android_drawable(icons_dir, resource_name, image_data)
            downloaded += 1
            print(f"  {progress} ✅ {resource_name:<14}")
        except Exception as e:
            print(f"  {progress} ❌ {resource_name:<14} → {e}")

        if i < len(coins):
            time.sleep(DOWNLOAD_DELAY)

    print(f"  ✅ Icons installed to: {icons_dir}")
    return downloaded


# ── Main ──────────────────────────────────────────────────────────────────────

def main() -> None:
    args = parse_args()
    project_root, icons_dir, rates_dir = get_platform_paths(args.platform)

    print("=" * 60)
    print(f"  Crypto Data Updater ({args.platform})")
    print("=" * 60)

    if not os.path.isdir(project_root):
        print(f"\n❌  Project root not found: {project_root}")
        sys.exit(1)

    # 1. Fetch data
    print(f"\n[1/5] Fetching top {ICON_COUNT} assets from CoinGecko …")
    coins = fetch_coin_list(ICON_COUNT)
    print(f"  ✅ {len(coins)} assets retrieved.")

    # 2. Fetch fiat rates
    fetch_fiat_rates(rates_dir)

    # 3. Handle Crypto Rates JSON
    print(f"\n[3/5] Updating {CRYPTO_RATES_FILENAME} …")
    os.makedirs(rates_dir, exist_ok=True)
    rates_file_path = os.path.join(rates_dir, CRYPTO_RATES_FILENAME)

    # Delete old file if it exists
    if os.path.exists(rates_file_path):
        os.remove(rates_file_path)
        print(f"  🗑  Deleted old {CRYPTO_RATES_FILENAME}")

    # Map data to requested structure
    rates_data = []
    for c in coins:
        rates_data.append({
            "id": c.get("id"),
            "symbol": c.get("symbol"),
            "name": c.get("name"),
            "current_price": c.get("current_price"),
            "market_cap": c.get("market_cap"),
            "market_cap_rank": c.get("market_cap_rank")
        })

    with open(rates_file_path, "w") as f:
        json.dump(rates_data, f, indent=2)
    print(f"  ✅ Saved {len(rates_data)} rates to {rates_dir}")

    downloaded = 0
    if args.download_icons:
        if args.platform == "ios":
            downloaded = download_ios_icons(coins, icons_dir)
        else:
            downloaded = download_android_icons(coins, icons_dir)
    else:
        print("\n[4/5] Skipping icons. Use --download-icons to update crypto icons.")

    print("\n" + "=" * 60)
    if args.download_icons:
        print(f"  Done! Rates saved and {downloaded} icons updated.")
    else:
        print("  Done! Rates saved.")
    print("=" * 60)


if __name__ == "__main__":
    main()
