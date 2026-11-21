import ActivityKit
import SwiftUI
import WidgetKit

@main
struct RichtingWidgets: WidgetBundle {
    var body: some Widget {
        RichtingNextTrainWidget()
        RichtingLiveActivity()
    }
}

struct RichtingLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: NextTrainAttributes.self) { context in
            VStack(alignment: .leading, spacing: 6) {
                Label("Richting", systemImage: "tram.fill").font(.caption.bold())
                LabeledContent(context.attributes.sourceTrainStopName, value: context.state.departureFromSourceTime)
                LabeledContent(context.attributes.destinationTrainStopName, value: context.state.arrivalAtDestinationTime)
                if context.isStale { Text("Open Richting for updated times.").font(.caption) }
            }
            .padding().monospacedDigit()
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) { Image(systemName: "tram.fill") }
                DynamicIslandExpandedRegion(.trailing) { Text(context.state.departureFromSourceTime) }
                DynamicIslandExpandedRegion(.bottom) {
                    LabeledContent(context.attributes.destinationTrainStopName, value: context.state.arrivalAtDestinationTime)
                }
            } compactLeading: { Image(systemName: "tram.fill") }
              compactTrailing: { Text(context.state.departureFromSourceTime).monospacedDigit() }
              minimal: { Image(systemName: "tram.fill") }
        }
    }
}
