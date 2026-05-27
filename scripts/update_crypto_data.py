#!/usr/bin/env python3
"""
update_crypto_data.py

1. Fetches top N cryptocurrencies from CoinGecko API
2. Fetches fiat rates from OpenExchangeRates
3. Downloads matching Android crypto icons
4. Validates all top 200 crypto items have rates and icons
5. Atomically installs rates and icons into the data branch worktree
"""

import os
import sys
import json
import shutil
import time
import re
import urllib.request
import urllib.error

# ── Configuration ─────────────────────────────────────────────────────────────

PROJECT_ROOT = os.environ.get(
    "CI_PROJECT_ROOT",
    os.path.dirname(os.path.abspath(__file__))
)
OPENEXCHANGERATES_API_KEY = os.environ.get("OPENEXCHANGERATES_API_KEY", "")
ANDROID_ICONS_DIR = os.path.join(PROJECT_ROOT, "cryptoicons", "src", "main", "res", "drawable")
ANDROID_RATES_DIR = os.path.join(PROJECT_ROOT, "core", "data", "src", "main", "assets")
CRYPTO_RATES_FILENAME = "crypto-rates.json"
FIAT_RATES_FILENAME = "fiat-rates.json"

CRYPTO_ASSET_LIMIT = 200
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"

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
    if total is not None and len(coins) != total:
        raise RuntimeError(f"Expected {total} crypto assets, got {len(coins)}")
    return coins


def create_android_drawable(parent_dir: str, asset_name: str, image_bytes: bytes) -> None:
    if not image_bytes.startswith(PNG_SIGNATURE):
        raise RuntimeError(f"{asset_name} icon is not a PNG")

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


def fetch_fiat_rates() -> dict:
    if not OPENEXCHANGERATES_API_KEY:
        raise RuntimeError("OPENEXCHANGERATES_API_KEY env variable is required")

    print(f"\n[2/5] Fetching {FIAT_RATES_FILENAME} …")
    url = OPENEXCHANGERATES_URL.format(app_id=OPENEXCHANGERATES_API_KEY)
    try:
        raw = fetch_with_retry(url, label="OpenExchangeRates")
    except Exception as e:
        print(f"  ❌ Failed to fetch fiat rates: {e}")
        sys.exit(1)

    fiat_data = json.loads(raw.decode())
    rates = fiat_data.get("rates", {})
    if not isinstance(rates, dict) or not rates:
        raise RuntimeError("OpenExchangeRates response does not contain rates")

    print(f"  ✅ Fetched {len(rates)} fiat rates.")
    return fiat_data


def validate_crypto_assets(
    coins: list[dict],
    icons_dir: str | None = None,
    expected_count: int = CRYPTO_ASSET_LIMIT,
) -> None:
    if len(coins) != expected_count:
        raise RuntimeError(f"Expected {expected_count} crypto assets, got {len(coins)}")

    resource_names: set[str] = set()
    for index, coin in enumerate(coins, start=1):
        symbol = coin.get("symbol")
        if not symbol:
            raise RuntimeError(f"Missing crypto symbol at item {index}")
        if coin.get("current_price") is None:
            raise RuntimeError(f"Missing current_price for {symbol}")

        resource_name = android_resource_name(symbol)
        if resource_name in resource_names:
            raise RuntimeError(f"Duplicate Android icon resource name: {resource_name}")
        resource_names.add(resource_name)

        if icons_dir is not None:
            icon_path = os.path.join(icons_dir, f"{resource_name}.png")
            if not os.path.exists(icon_path):
                raise RuntimeError(f"Missing PNG icon for {symbol}: {icon_path}")
            with open(icon_path, "rb") as icon_file:
                if icon_file.read(len(PNG_SIGNATURE)) != PNG_SIGNATURE:
                    raise RuntimeError(f"{resource_name} icon is not a PNG")


def prepare_android_icons(coins: list[dict], tmp_icons_dir: str) -> int:
    print(f"\n[3/5] Downloading matching Android icons …")
    clean_directory(tmp_icons_dir)

    downloaded = 0
    for i, coin in enumerate(coins, start=1):
        ticker = coin.get("symbol", "")
        resource_name = android_resource_name(ticker)
        image_url = coin.get("image", "")
        progress = f"[{i}/{len(coins)}]"

        if not image_url:
            raise RuntimeError(f"Missing icon URL for {ticker or coin.get('id')}")

        try:
            image_data = fetch_with_retry(image_url, label=resource_name)
            create_android_drawable(tmp_icons_dir, resource_name, image_data)
            downloaded += 1
            print(f"  {progress} ✅ {resource_name:<14}")
        except Exception as e:
            raise RuntimeError(f"Failed to download icon {resource_name}: {e}") from e

        if i < len(coins):
            time.sleep(DOWNLOAD_DELAY)

    if downloaded != len(coins):
        raise RuntimeError(f"Expected {len(coins)} icons, downloaded {downloaded}")

    validate_crypto_assets(coins, tmp_icons_dir, expected_count=len(coins))
    print(f"  ✅ Prepared {downloaded} icons.")
    return downloaded


def crypto_rates_data(coins: list[dict]) -> list[dict]:
    return [
        {
            "id": c.get("id"),
            "symbol": c.get("symbol"),
            "name": c.get("name"),
            "current_price": c.get("current_price"),
            "market_cap": c.get("market_cap"),
            "market_cap_rank": c.get("market_cap_rank"),
        }
        for c in coins
    ]


def write_json(path: str, data: object) -> None:
    with open(path, "w") as f:
        json.dump(data, f, indent=2)


def replace_directory(prepared_dir: str, target_dir: str) -> None:
    backup_dir = target_dir + ".backup"
    if os.path.exists(backup_dir):
        shutil.rmtree(backup_dir)
    if os.path.exists(target_dir):
        shutil.move(target_dir, backup_dir)
    shutil.move(prepared_dir, target_dir)
    shutil.rmtree(backup_dir, ignore_errors=True)


def install_prepared_data(
    fiat_data: dict,
    crypto_data: list[dict],
    prepared_icons_dir: str,
    rates_dir: str,
    icons_dir: str,
) -> None:
    print("\n[5/5] Installing rates and icons together …")
    os.makedirs(rates_dir, exist_ok=True)
    tmp_rates_dir = rates_dir + ".tmp"
    clean_directory(tmp_rates_dir)

    write_json(os.path.join(tmp_rates_dir, FIAT_RATES_FILENAME), fiat_data)
    write_json(os.path.join(tmp_rates_dir, CRYPTO_RATES_FILENAME), crypto_data)

    for filename in (FIAT_RATES_FILENAME, CRYPTO_RATES_FILENAME):
        os.replace(
            os.path.join(tmp_rates_dir, filename),
            os.path.join(rates_dir, filename),
        )
    shutil.rmtree(tmp_rates_dir, ignore_errors=True)
    replace_directory(prepared_icons_dir, icons_dir)
    print("  ✅ Installed synced rates and icons.")


# ── Main ──────────────────────────────────────────────────────────────────────

def main() -> None:
    print("=" * 60)
    print("  Android Rates & Icons Updater")
    print("=" * 60)

    if not os.path.isdir(PROJECT_ROOT):
        print(f"\n❌  Project root not found: {PROJECT_ROOT}")
        sys.exit(1)

    print(f"\n[1/5] Fetching top {CRYPTO_ASSET_LIMIT} assets from CoinGecko …")
    coins = fetch_coin_list(CRYPTO_ASSET_LIMIT)
    validate_crypto_assets(coins)
    print(f"  ✅ {len(coins)} assets retrieved and validated.")

    fiat_data = fetch_fiat_rates()

    tmp_icons_dir = ANDROID_ICONS_DIR + ".tmp"
    downloaded = prepare_android_icons(coins, tmp_icons_dir)

    print(f"\n[4/5] Preparing {CRYPTO_RATES_FILENAME} …")
    crypto_data = crypto_rates_data(coins)
    print(f"  ✅ Prepared {len(crypto_data)} crypto rates.")
    install_prepared_data(
        fiat_data,
        crypto_data,
        tmp_icons_dir,
        ANDROID_RATES_DIR,
        ANDROID_ICONS_DIR,
    )

    print("\n" + "=" * 60)
    print(f"  Done! {len(crypto_data)} crypto rates and {downloaded} icons updated.")
    print("=" * 60)


if __name__ == "__main__":
    main()
