import SwiftUI

@main
struct RichtingApp: App {
    @State private var model = JourneyViewModel(catalog: .shared, service: TrainService.shared)

    var body: some Scene {
        WindowGroup { JourneyView(model: model) }
    }
}
