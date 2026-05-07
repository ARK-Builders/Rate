#!/usr/bin/env python3
"""
update_crypto_data.py

1. Fetches top N cryptocurrencies from CoinGecko API
2. Cleans existing icons and old rate data
3. Downloads icons as .imageset into Assets.xcassets
4. Saves market rates (price, cap, rank) to crypto-rates.json
5. Fetches fiat rates from OpenExchangeRates and saves to fiat-rates.json
"""

import os
import sys
import json
import shutil
import time
import urllib.request
import urllib.error

# ── Configuration ─────────────────────────────────────────────────────────────

PROJECT_ROOT = os.environ.get(
    "CI_PROJECT_ROOT",
    os.path.expanduser("~/Desktop/Rate-iOS/ARK Rate")
)

ASSETS_CRYPTO_DIR = os.path.join(PROJECT_ROOT, "Assets.xcassets", "cryptoicons")
# New: Resources directory for JSON data
RESOURCES_DIR = os.path.join(PROJECT_ROOT, "Resources")
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

def clean_directory(path: str) -> None:
    if os.path.exists(path):
        shutil.rmtree(path)
        print(f"  🗑  Cleaned: {path}")
    os.makedirs(path, exist_ok=True)
    print(f"  📁 Created: {path}")


def fetch_with_retry(url: str, label: str = "") -> bytes:
    headers = {"User-Agent": "XcodeCryptoIconUpdater/1.0"}
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


def create_imageset(parent_dir: str, asset_name: str, image_bytes: bytes) -> None:
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


def fetch_fiat_rates() -> None:
    app_id = os.environ.get("APP_ID", "")
    if not app_id:
        print("  ⚠️  APP_ID env variable not set — skipping fiat rates.")
        return

    print(f"\n[2/5] Updating {FIAT_RATES_FILENAME} …")
    os.makedirs(RESOURCES_DIR, exist_ok=True)
    fiat_file_path = os.path.join(RESOURCES_DIR, FIAT_RATES_FILENAME)

    if os.path.exists(fiat_file_path):
        os.remove(fiat_file_path)
        print(f"  🗑  Deleted old {FIAT_RATES_FILENAME}")

    url = OPENEXCHANGERATES_URL.format(app_id=app_id)
    try:
        raw = fetch_with_retry(url, label="OpenExchangeRates")
    except Exception as e:
        print(f"  ❌ Failed to fetch fiat rates: {e}")
        sys.exit(1)

    fiat_data = json.loads(raw.decode())

    with open(fiat_file_path, "w") as f:
        json.dump(fiat_data, f, indent=2)
    print(f"  ✅ Saved fiat rates to {fiat_file_path}")


# ── Main ──────────────────────────────────────────────────────────────────────

def main() -> None:
    print("=" * 60)
    print("  Xcode Crypto Icon & Rate Updater")
    print("=" * 60)

    if not os.path.isdir(PROJECT_ROOT):
        print(f"\n❌  Project root not found: {PROJECT_ROOT}")
        sys.exit(1)

    # 1. Fetch data
    print(f"\n[1/5] Fetching top {ICON_COUNT} assets from CoinGecko …")
    coins = fetch_coin_list(ICON_COUNT)
    print(f"  ✅ {len(coins)} assets retrieved.")

    # 2. Fetch fiat rates
    fetch_fiat_rates()

    # 3. Handle Crypto Rates JSON
    print(f"\n[3/5] Updating {CRYPTO_RATES_FILENAME} …")
    # Ensure Resources directory exists
    os.makedirs(RESOURCES_DIR, exist_ok=True)
    rates_file_path = os.path.join(RESOURCES_DIR, CRYPTO_RATES_FILENAME)
    
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
    print(f"  ✅ Saved {len(rates_data)} rates to {RESOURCES_DIR}")

    # 4. Download icons
    print(f"\n[4/5] Downloading icons …")
    tmp_dir = ASSETS_CRYPTO_DIR + ".tmp"
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
            create_imageset(tmp_dir, ticker, image_data)
            downloaded += 1
            print(f"  {progress} ✅ {ticker:<10}")
        except Exception as e:
            print(f"  {progress} ❌ {ticker:<10} → {e}")

        if i < len(coins):
            time.sleep(DOWNLOAD_DELAY)

    # 5. Replace live icons
    print("\n[5/5] Finalizing Asset Catalog …")
    clean_directory(ASSETS_CRYPTO_DIR)
    
    # Re-add the main Contents.json for the cryptoicons folder
    with open(os.path.join(ASSETS_CRYPTO_DIR, "Contents.json"), "w") as f:
        json.dump({"info": {"author": "xcode", "version": 1}}, f, indent=2)

    for item in os.listdir(tmp_dir):
        if item.endswith(".imageset"):
            shutil.move(os.path.join(tmp_dir, item), os.path.join(ASSETS_CRYPTO_DIR, item))

    shutil.rmtree(tmp_dir, ignore_errors=True)
    print(f"  ✅ Icons installed to: {ASSETS_CRYPTO_DIR}")

    print("\n" + "=" * 60)
    print(f"  Done! Rates saved and {downloaded} icons updated.")
    print("=" * 60)


if __name__ == "__main__":
    main()