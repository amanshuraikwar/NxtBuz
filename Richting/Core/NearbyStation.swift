import CoreLocation
import Foundation

enum NearbyError: LocalizedError {
    case permission, unavailable
    var errorDescription: String? {
        switch self {
        case .permission: "Allow location for Richting and its widgets to find your nearest station."
        case .unavailable: "Your current location is unavailable. Try again nearby."
        }
    }
}

@MainActor
final class LocationFix: NSObject, @preconcurrency CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    private var continuation: CheckedContinuation<CLLocation, Error>?
    private var timer: Task<Void, Never>?
    private var requested = false

    func current(requestPermission: Bool, forWidget: Bool = false) async throws -> CLLocation {
        if forWidget && !manager.isAuthorizedForWidgetUpdates { throw NearbyError.permission }
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        return try await withCheckedThrowingContinuation { continuation in
            self.continuation = continuation
            timer = Task {
                try? await Task.sleep(for: .seconds(12))
                guard !Task.isCancelled else { return }
                finish(.failure(NearbyError.unavailable))
            }
            if manager.authorizationStatus == .notDetermined && requestPermission {
                manager.requestWhenInUseAuthorization()
            } else if manager.authorizationStatus == .notDetermined {
                finish(.failure(NearbyError.permission))
            } else {
                locate()
            }
        }
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        if continuation != nil && manager.authorizationStatus != .notDetermined { locate() }
    }

    private func locate() {
        guard manager.authorizationStatus == .authorizedAlways || manager.authorizationStatus == .authorizedWhenInUse else {
            finish(.failure(NearbyError.permission))
            return
        }
        if let cached = manager.location, usable(cached) { finish(.success(cached)); return }
        if !requested { requested = true; manager.requestLocation() }
    }

    private func usable(_ location: CLLocation) -> Bool {
        location.horizontalAccuracy >= 0 && abs(location.timestamp.timeIntervalSinceNow) <= 300
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let fix = locations.last(where: usable) { finish(.success(fix)) }
        else { finish(.failure(NearbyError.unavailable)) }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        finish(.failure((error as? CLError)?.code == .denied ? NearbyError.permission : NearbyError.unavailable))
    }

    private func finish(_ result: Result<CLLocation, Error>) {
        timer?.cancel()
        timer = nil
        manager.delegate = nil
        continuation?.resume(with: result)
        continuation = nil
    }
}
