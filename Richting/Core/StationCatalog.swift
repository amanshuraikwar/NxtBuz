import CoreLocation
import Foundation
import SQLite3

struct Station: Identifiable, Hashable, Sendable {
    let id: String
    let name: String
    let latitude: Double
    let longitude: Double
    let hasDepartures: Bool
    let searchKey: String

    func matches(_ query: String) -> Bool {
        let normalized = query.filter { $0.isLetter || $0.isNumber }
        return query.isEmpty || id.localizedCaseInsensitiveContains(query)
            || name.localizedStandardContains(query)
            || (!normalized.isEmpty && searchKey.localizedCaseInsensitiveContains(normalized))
    }
}

enum CatalogError: LocalizedError {
    case unreadable
    var errorDescription: String? { "The station catalog could not be opened." }
}

actor StationCatalog {
    static let shared = StationCatalog()
    private var cached: [Station]?

    func closest(to location: CLLocation) throws -> Station {
        let candidates = try stations().filter(\.hasDepartures)
        func distance(_ station: Station) -> CLLocationDistance {
            location.distance(from: CLLocation(latitude: station.latitude, longitude: station.longitude))
        }
        guard let nearest = candidates.min(by: { distance($0) < distance($1) }) else {
            throw CatalogError.unreadable
        }
        return nearest
    }

    func stations() throws -> [Station] {
        if let cached { return cached }
        guard let url = Bundle.main.url(forResource: "nsapi", withExtension: "db") else {
            throw CatalogError.unreadable
        }
        var db: OpaquePointer?
        defer { sqlite3_close(db) }
        guard sqlite3_open_v2(url.path, &db, SQLITE_OPEN_READONLY, nil) == SQLITE_OK else {
            throw CatalogError.unreadable
        }
        var statement: OpaquePointer?
        defer { sqlite3_finalize(statement) }
        let sql = "SELECT code, nameLong, descriptionSearchKey, latitude, longitude, hasDepartureTimes FROM NsTrainStationEntity WHERE land = 'NL' ORDER BY nameLong COLLATE NOCASE"
        guard sqlite3_prepare_v2(db, sql, -1, &statement, nil) == SQLITE_OK else { throw CatalogError.unreadable }
        var result: [Station] = []
        var status = sqlite3_step(statement)
        while status == SQLITE_ROW {
            result.append(Station(id: String(cString: sqlite3_column_text(statement, 0)),
                                  name: String(cString: sqlite3_column_text(statement, 1)),
                                  latitude: sqlite3_column_double(statement, 3),
                                  longitude: sqlite3_column_double(statement, 4),
                                  hasDepartures: sqlite3_column_int(statement, 5) != 0,
                                  searchKey: String(cString: sqlite3_column_text(statement, 2))))
            status = sqlite3_step(statement)
        }
        guard status == SQLITE_DONE, !result.isEmpty else { throw CatalogError.unreadable }
        cached = result
        return result
    }
}
