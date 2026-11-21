"""Build the bundled catalog from a v2 NS stations API response saved as JSON."""
import argparse
import json
from pathlib import Path
import re
import sqlite3
import tempfile

ROOT = Path(__file__).resolve().parents[2]
SCHEMA = ROOT / "nsapi/src/commonMain/sqldelight/io/github/amanshuraikwar/nsapi/db/NsTrainStationEntity.sq"


def build(source, destination):
    stations = sorted(
        (s for s in json.loads(source.read_text())["payload"] if s["land"] == "NL"),
        key=lambda s: s["code"],
    )
    if not stations:
        raise ValueError("The station response contains no Dutch stations")
    # Use the same table definition as SQLDelight's onboarding cache.
    ddl = SCHEMA.read_text().split(";", 1)[0].replace("INTEGER AS Boolean", "INTEGER")
    with tempfile.TemporaryDirectory(dir=destination.parent) as temp:
        output = Path(temp) / "nsapi.db"
        db = sqlite3.connect(output)
        try:
            with db:
                db.execute(ddl)
                db.execute("CREATE TABLE StationPayload (code TEXT PRIMARY KEY, json TEXT NOT NULL)")
                for s in stations:
                    names = s["namen"]
                    nearby = s["nearbyMeLocationId"]
                    key = re.sub("[^a-z0-9]", "", (names["kort"] + names["middel"] + names["lang"]).lower())
                    db.execute(
                        "INSERT INTO NsTrainStationEntity VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        (s["stationType"], s["code"], s["EVACode"], s["UICCode"], s["heeftFaciliteiten"],
                         s["heeftVertrektijden"], s["heeftReisassistentie"], names["kort"], names["middel"],
                         names["lang"], s["land"], s["lat"], s["lng"], s["ingangsDatum"], nearby["type"],
                         nearby["value"], False, key),
                    )
                    db.execute("INSERT INTO StationPayload VALUES (?,?)",
                               (s["code"], json.dumps(s, ensure_ascii=False, separators=(",", ":"), sort_keys=True)))
                db.execute("PRAGMA user_version = 1")
        finally:
            db.close()
        output.replace(destination)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("response", type=Path)
    args = parser.parse_args()
    build(args.response, ROOT / "Richting/Resources/nsapi.db")
