import Foundation
import RichtingTrains

struct Journey: Equatable, Sendable {
    let from: String
    let to: String
    let departureDate: Date
    let arrivalDate: Date?
    let service: String
    let platform: String?
    let trainNumber: String
    let facilities: [String]
    let imageURLs: [URL]

    var departure: String { Self.time(departureDate) }
    var arrival: String { arrivalDate.map(Self.time) ?? "—" }

    static func time(_ date: Date) -> String {
        date.formatted(Date.VerbatimFormatStyle(format: "\(hour: .twoDigits(clock: .twentyFourHour, hourCycle: .zeroBased)):\(minute: .twoDigits)",
            locale: Locale(identifier: "nl_NL"), timeZone: TimeZone(identifier: "Europe/Amsterdam")!,
            calendar: Calendar(identifier: .gregorian)))
    }
}

enum TrainError: LocalizedError {
    case configuration, unavailable
    var errorDescription: String? {
        switch self {
        case .configuration: "Live departures need an NS subscription key."
        case .unavailable: "Live train information is temporarily unavailable."
        }
    }
}

@MainActor
protocol TrainProviding {
    func upcoming(from: Station, to: Station) async throws -> [Journey]
}

@MainActor
final class TrainService: TrainProviding {
    static let shared = TrainService()
    private var board: StationBoard?

    private func stationBoard() throws -> StationBoard {
        if let board { return board }
        guard let config = Bundle.main.url(forResource: "NSConfig", withExtension: "plist"),
              let values = try PropertyListSerialization.propertyList(from: Data(contentsOf: config), format: nil) as? [String: String],
              let key = values["SubscriptionKey"], !key.isEmpty else { throw TrainError.configuration }
        let directory = try FileManager.default.url(for: .applicationSupportDirectory, in: .userDomainMask,
                                                     appropriateFor: nil, create: true)
        let board = StationBoard(key: key, directory: directory.path, suite: Bundle.main.bundleIdentifier ?? "Richting")
        self.board = board
        return board
    }

    func upcoming(from: Station, to: Station) async throws -> [Journey] {
        let board = try stationBoard()
        // Kotlin/Native 1.7 requires the exported suspend entry point to be called on the main thread.
        let trains: [BoardTrain] = try await withCheckedThrowingContinuation { continuation in
            board.departures(from: from.id, to: to.id) { trains, error in
                if let trains, error == nil { continuation.resume(returning: trains) }
                else { continuation.resume(throwing: TrainError.unavailable) }
            }
        }
        return trains.map { train in
            var facilities: [String] = []
            if train.hasWifi { facilities.append("wifi") }
            if train.hasBicycleSpace { facilities.append("bicycle") }
            if train.hasPower { facilities.append("powerplug") }
            return Journey(from: from.name, to: to.name,
                departureDate: Date(timeIntervalSince1970: Double(train.departureSeconds)),
                arrivalDate: train.arrivalSeconds.map { Date(timeIntervalSince1970: $0.doubleValue) },
                service: train.service, platform: train.platform, trainNumber: train.number,
                facilities: facilities, imageURLs: train.images.compactMap(URL.init(string:)))
        }
    }
}

enum RoutePreferences {
    static let defaults = UserDefaults(suiteName: "group.io.github.amanshuraikwar.Richting")
    static let homeKey = "homeStation"
}
