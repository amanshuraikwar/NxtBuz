import ActivityKit
import CoreLocation
import Foundation
import Observation
import WidgetKit

@MainActor
@Observable
final class JourneyViewModel {
    enum State: Equatable {
        case idle, locating, loading, loaded([Journey]), atHome, noTrain, failed(String)
    }

    private(set) var stations: [Station] = []
    private(set) var home: Station?
    private(set) var nearest: Station?
    private(set) var state: State = .idle
    private(set) var catalogError: String?
    private(set) var activityMessage: String?
    @ObservationIgnored private let catalog: StationCatalog
    @ObservationIgnored private let service: any TrainProviding
    @ObservationIgnored private let defaults: UserDefaults?
    @ObservationIgnored private var requestID = UUID()

    init(catalog: StationCatalog, service: any TrainProviding, defaults: UserDefaults? = RoutePreferences.defaults) {
        self.catalog = catalog
        self.service = service
        self.defaults = defaults
    }

    func loadStations() async {
        guard stations.isEmpty else { return }
        do {
            stations = try await catalog.stations()
            home = stations.first { $0.id == defaults?.string(forKey: RoutePreferences.homeKey) }
            catalogError = nil
        } catch {
            catalogError = error.localizedDescription
        }
    }

    func setHome(_ station: Station) {
        guard let defaults else {
            catalogError = "The shared home station settings are unavailable."
            return
        }
        home = station
        defaults.set(station.id, forKey: RoutePreferences.homeKey)
        requestID = UUID()
        state = .idle
        activityMessage = nil
        WidgetCenter.shared.reloadTimelines(ofKind: "Richting.NextTrain")
    }

    func refresh(requestPermission: Bool) async {
        guard let home else { return }
        let id = UUID()
        requestID = id
        state = .locating
        do {
            let fix = try await LocationFix().current(requestPermission: requestPermission)
            let station = try await catalog.closest(to: fix)
            guard id == requestID else { return }
            nearest = station
            if requestPermission { WidgetCenter.shared.reloadTimelines(ofKind: "Richting.NextTrain") }
            guard home != station else { state = .atHome; return }
            state = .loading
            let trains = try await service.upcoming(from: station, to: home)
            guard id == requestID else { return }
            state = trains.isEmpty ? .noTrain : .loaded(trains)
        } catch {
            if id == requestID { state = .failed(error.localizedDescription) }
        }
    }

    func track(_ journey: Journey) async {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else {
            activityMessage = "Enable Live Activities for Richting in Settings."
            return
        }
        let attributes = NextTrainAttributes(sourceTrainStopName: journey.from, destinationTrainStopName: journey.to)
        let content = ActivityContent(state: NextTrainAttributes.ContentState(departureFromSourceTime: journey.departure,
                                                                              arrivalAtDestinationTime: journey.arrival),
                                      staleDate: journey.departureDate)
        do {
            let existing = Activity<NextTrainAttributes>.activities
            if let current = existing.first(where: {
                $0.attributes.sourceTrainStopName == journey.from && $0.attributes.destinationTrainStopName == journey.to
            }) {
                await current.update(content)
            } else {
                _ = try Activity.request(attributes: attributes, content: content, pushType: nil)
                for activity in existing { await activity.end(nil, dismissalPolicy: .immediate) }
            }
            activityMessage = "Journey added to your Lock Screen."
        } catch {
            activityMessage = error.localizedDescription
        }
    }
}
