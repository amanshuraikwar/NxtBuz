"""Prepare one station catalog and NS configuration for the app and its widget."""
import os
from pathlib import Path
import plistlib
import shutil
import tempfile
import urllib.request

from build_station_catalog import build

root = Path(os.environ["SRCROOT"])
products = Path(os.environ["BUILT_PRODUCTS_DIR"]) / "RichtingResources"
products.mkdir(parents=True, exist_ok=True)
release = os.environ["CONFIGURATION"] == "Release"
key = os.environ.get("NS_API_SUBSCRIPTION_KEY", "").strip()
if not key:
    properties = root.parent / "local.properties"
    if properties.is_file():
        values = dict(line.strip().split("=", 1) for line in properties.read_text().splitlines()
                      if "=" in line and not line.lstrip().startswith("#"))
        key = values.get("nsapi.subscription.key." + ("release" if release else "debug"), "").strip()

with tempfile.TemporaryDirectory(dir=products) as temp:
    stage = Path(temp)
    if release:
        if not key:
            raise SystemExit("Set NS_API_SUBSCRIPTION_KEY or nsapi.subscription.key.release for Release builds.")
        request = urllib.request.Request("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2/stations",
                                         headers={"Ocp-Apim-Subscription-Key": key})
        with urllib.request.urlopen(request, timeout=45) as response:
            payload = stage / "stations.json"
            payload.write_bytes(response.read())
        build(payload, stage / "nsapi.db")
    else:
        shutil.copyfile(root / "Resources/nsapi.db", stage / "nsapi.db")
    (stage / "NSConfig.plist").write_bytes(plistlib.dumps({"SubscriptionKey": key}))
    for name in ("nsapi.db", "NSConfig.plist"):
        (stage / name).replace(products / name)
