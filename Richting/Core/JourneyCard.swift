import SwiftUI

struct JourneyCard: View {
    let journey: Journey
    var stockImage: Image? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(journey.firstLeg.service + " · " + journey.transferSummary).font(.caption.weight(.semibold))
                Spacer()
                if let platform = journey.firstLeg.platform { Text("Platform " + platform).font(.caption) }
            }
            .foregroundStyle(.secondary)
            row(journey.departure, journey.from, symbol: "circle")
            ForEach(journey.onwardLegs.indices, id: \.self) { index in
                let leg = journey.onwardLegs[index]
                VStack(alignment: .leading, spacing: 2) {
                    row(Journey.time(leg.departure), leg.from, symbol: leg.isWalk ? "figure.walk" : "arrow.left.arrow.right")
                    let service = [leg.service, leg.number].compactMap { $0 }.joined(separator: " ")
                    Text([service, leg.platform.map { "Platform " + $0 }].compactMap { $0 }.joined(separator: " · "))
                        .font(.caption).foregroundStyle(.secondary)
                }
            }
            row(journey.arrival, journey.to, symbol: "circle.fill")
            HStack {
                if let stockImage {
                    stockImage.resizable().scaledToFit().frame(maxWidth: 96, maxHeight: 18)
                } else {
                    Image(systemName: "tram.fill")
                }
                if let number = journey.firstLeg.number { Text(number) }
                Spacer()
                ForEach(journey.facilities, id: \.self) { Image(systemName: $0) }
            }
            .font(.caption).foregroundStyle(.secondary)
        }
    }

    private func row(_ time: String, _ name: String, symbol: String) -> some View {
        HStack {
            Text(time).monospacedDigit().bold()
            Image(systemName: symbol).font(.caption2).foregroundStyle(.indigo)
            Text(name).lineLimit(1).minimumScaleFactor(0.8)
        }
        .accessibilityElement(children: .combine)
    }
}
