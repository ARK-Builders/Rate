import os
import json
import requests
import time

crypto_file = "crypto.json"
output_file = "crypto-rates.json"

data_dir = "coin_data"
full_rates_file = "all_rates_from_API.json"

def fetch_and_save_data(page_number):
    print(f"Fetching page {page_number}...")
    file_path = os.path.join(data_dir, f"coin_data_{page_number}.json")

    if os.path.exists(file_path):
        print(f"File {file_path} already exists. Skipping download.")
        return
    else:
        time.sleep(5)
        url = f"https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=250&page={page_number}"
        headers = {
            "accept": "application/json",
            "x-cg-demo-api-key": "CG-2VJGW66iRL8Wu3NfPP8VgsWS",
        }
        try:
            response = requests.get(url, headers=headers)
            data = response.json()
            with open(file_path, "w", encoding="utf-8") as f:
                json.dump(data, f, indent=4)
            print(f"Data for page {page_number} saved to {file_path}")
        except Exception as e:
            print(f"Error fetching data for page {page_number}: {e}")
            return

def fetch_all_data():
    os.makedirs(data_dir, exist_ok=True)

    while len(os.listdir(data_dir)) < 69:
        for n in range(1, 70):
            fetch_and_save_data(n)

    merged_data = []
    for n in range(1, 70):
        page_file = os.path.join(data_dir, f"coin_data_{n}.json")
        with open(page_file, "r", encoding="utf-8") as f:
            try:
                data = json.load(f)
                if isinstance(data, list):
                    merged_data.extend(data)
                else:
                    print(f"Warning: {page_file} did not contain a list. Skipping.")
            except json.JSONDecodeError:
                print(f"Warning: {page_file} could not be decoded. Skipping.")

    merged_data = [item for item in merged_data if item.get('current_price') is not None]

    with open(full_rates_file, "w", encoding="utf-8") as f:
        json.dump(merged_data, f, indent=4)
        
    print(f"Merged data saved to {full_rates_file}")

def reduce_data():

    with open(full_rates_file, "r", encoding="utf-8") as f:
        data = json.load(f)

    with open(crypto_file, "r", encoding="utf-8") as f:
        crypto_data = json.load(f)

    crypto_ids = [key.lower() for key in crypto_data.keys()]

    keys_to_keep = ["id", "symbol", "name", "current_price", "market_cap", "market_cap_rank"]

    #reduced data to new_data
    #keep track of unique symbols to ensure no duplicates
    new_data = []
    symbols_set = set()

    for item in data:

        #option for if we dont want to filter based on crypto.json
        #if item['symbol'] not in symbols_set:

        #unique 'symbols' and 'symbol' is in crypto.json file
        if item['symbol'] not in symbols_set and item['symbol'] in crypto_ids:
            item_filtered = {key: item[key] for key in keys_to_keep if key in item}
            new_data.append(item_filtered)
            symbols_set.add(item['symbol'])
        else:
            #print(f"Duplicate found: {item['symbol']}")
            continue
            
    #option to filter for top 1000
    #new_data = new_data[:999]
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(new_data, f)

def main():
    # Step 1: Fetch and merge API data
    fetch_all_data()

    # Step 2: Reduce the merged data
    reduce_data()

if __name__ == "__main__":
    main()
