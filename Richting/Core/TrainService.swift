import Foundation
import RichtingTrains

struct JourneyLeg: Equatable, Sendable {
    let from: String
    let to: String
    let departure: Date
    let arrival: Date
    let service: String
    let platform: String?
    let number: String?
    let isWalk: Bool
    let imageURLs: [URL]
}

private extension JourneyLeg {
    init(_ leg: BoardLeg) {
        from = leg.from
        to = leg.to
        departure = Date(timeIntervalSince1970: Double(leg.departureSeconds))
        arrival = Date(timeIntervalSince1970: Double(leg.arrivalSeconds))
        service = leg.service
        platform = leg.platform
        number = leg.number
        imageURLs = leg.images.compactMap(URL.init(string:))
        isWalk = leg.isWalk
    }
}

struct Journey: Identifiable, Equatable, Sendable {
    let id: String
    let firstLeg: JourneyLeg
    let onwardLegs: [JourneyLeg]
    let transfers: Int
    let facilities: [String]

    var trainLegs: [JourneyLeg] { [firstLeg] + onwardLegs.filter { !$0.isWalk } }
    var from: String { firstLeg.from }
    var to: String { (onwardLegs.last ?? firstLeg).to }
    var departureDate: Date { firstLeg.departure }
    var departure: String { Self.time(departureDate) }
    var arrival: String { Self.time((onwardLegs.last ?? firstLeg).arrival) }
    var transferSummary: String {
        switch transfers {
        case 0: "Direct"
        case 1: "1 change"
        default: "\(transfers) changes"
        }
    }

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
        let trips: [BoardJourney] = try await withCheckedThrowingContinuation { continuation in
            board.journeys(from: from.id, to: to.id) { trips, error in
                if let trips, error == nil { continuation.resume(returning: trips) }
                else { continuation.resume(throwing: TrainError.unavailable) }
            }
        }
        return trips.compactMap { trip in
            let legs = trip.legs.map(JourneyLeg.init)
            guard let first = legs.first else { return nil }
            return Journey(id: trip.id, firstLeg: first, onwardLegs: Array(legs.dropFirst()),
                           transfers: Int(trip.transfers), facilities: trip.facilities)
        }
    }
}

enum RoutePreferences {
    static let defaults = UserDefaults(suiteName: "group.io.github.amanshuraikwar.Richting")
    static let homeKey = "homeStation"
}
