import SwiftUI
import WidgetKit

struct TrainEntry: TimelineEntry {
    let date: Date
    let journeys: [Journey]
    let message: String
    var images: [UIImage] = []
}

struct NextTrainProvider: TimelineProvider {
    func placeholder(in context: Context) -> TrainEntry {
        let journeys = [5, 20].map { minutes in
            let leg = JourneyLeg(from: "Amsterdam Centraal", to: "Utrecht Centraal",
                                 departure: .now.addingTimeInterval(Double(minutes * 60)),
                                 arrival: .now.addingTimeInterval(Double((minutes + 27) * 60)),
                                 service: "Intercity", platform: "5", number: "829", isWalk: false)
            return Journey(id: String(minutes), firstLeg: leg, onwardLegs: [], transfers: 0,
                           facilities: ["wifi"], imageURLs: [])
        }
        return TrainEntry(date: .now, journeys: journeys, message: "")
    }

    func getSnapshot(in context: Context, completion: @escaping (TrainEntry) -> Void) {
        if context.isPreview { completion(placeholder(in: context)); return }
        Task { completion(await entry()) }
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<TrainEntry>) -> Void) {
        Task { completion(await timeline()) }
    }

    private func timeline() async -> Timeline<TrainEntry> {
        let first = await entry()
        var entries = [first]
        for departure in Set(first.journeys.map(\.departureDate)).sorted() {
            let after = departure.addingTimeInterval(1)
            entries.append(TrainEntry(date: after, journeys: first.journeys.filter { $0.departureDate >= after },
                                      message: "Checking for later trains…"))
        }
        let refresh = min(first.journeys.first?.departureDate ?? .distantFuture, Date().addingTimeInterval(300))
        return Timeline(entries: entries, policy: .after(max(refresh, Date().addingTimeInterval(60))))
    }

    private func entry() async -> TrainEntry {
        do {
            let destinationID = RoutePreferences.defaults?.string(forKey: RoutePreferences.homeKey)
            guard let to = try await StationCatalog.shared.stations().first(where: { $0.id == destinationID }) else {
                return TrainEntry(date: .now, journeys: [], message: "Open Richting to set your home station.")
            }
            let fix = try await LocationFix().current(requestPermission: false, forWidget: true)
            let from = try await StationCatalog.shared.closest(to: fix)
            guard from != to else {
                return TrainEntry(date: .now, journeys: [], message: "\(to.name) is already your nearest station.")
            }
            let trains = try await TrainService.shared.upcoming(from: from, to: to)
            let images = await stockImages(for: trains.first)
            let now = Date()
            let upcoming = trains.filter { $0.departureDate > now }
            return TrainEntry(date: now, journeys: upcoming,
                              message: "No rail journeys from \(from.name) to \(to.name).",
                              images: upcoming.first?.id == trains.first?.id ? images : [])
        } catch {
            return TrainEntry(date: .now, journeys: [], message: error.localizedDescription)
        }
    }
    // Download identical coaches once and preserve the train's original carriage order.
    private func stockImages(for train: Journey?) async -> [UIImage] {
        let urls = (train?.imageURLs ?? []).prefix(8).filter { $0.scheme == "https" }
        return await withTaskGroup(of: (URL, Data?).self) { group in
            for url in Set(urls) {
                group.addTask {
                    let request = URLRequest(url: url, timeoutInterval: 2)
                    let data = try? await URLSession.shared.data(for: request).0
                    return (url, data)
                }
            }
            var images: [URL: UIImage] = [:]
            for await (url, data) in group {
                if let data, let image = UIImage(data: data), image.cgImage != nil {
                    images[url] = image
                }
            }
            return urls.compactMap { images[$0] }
        }
    }

}

struct RichtingTrainWidgetView: View {
    let entry: TrainEntry
    @Environment(\.colorScheme) var scheme
    private var dark: Bool { scheme == .dark }
    private var accent: Color { dark ? Color(red: 0.11, green: 0.70, blue: 0.95) : Color(red: 0.09, green: 0.42, blue: 0.90) }

    var body: some View {
        if let first = entry.journeys.first {
            GeometryReader { geometry in
                VStack(alignment: .leading, spacing: 4) {
                    TrainRoutePanel(from: first.from, to: first.to,
                        times: entry.journeys.map { .init(departure: $0.departure, arrival: $0.arrival, platform: $0.firstLeg.platform, detail: $0.transferSummary) },
                        accent: accent, primary: Color(red: 0.15, green: 0.20, blue: 0.22),
                        secondary: dark ? Color(.lightGray) : Color(red: 0.27, green: 0.35, blue: 0.39), isDark: dark)
                    HStack {
                        if first.transfers > 0 {
                            let changes = first.onwardLegs.filter { !$0.isWalk }.map(\.from)
                            Text("Via " + changes.joined(separator: " · "))
                                .lineLimit(1).minimumScaleFactor(0.75)
                        } else {
                            Text(Journey.time(entry.date)).font(NxtBuzFonts.caption)
                        }
                        Spacer()
                        ForEach(first.facilities, id: \.self) { Image(systemName: $0) }
                        if let number = first.firstLeg.number { Text(number).bold() }
                    }
                    .font(NxtBuzFonts.caption).foregroundStyle(accent).padding(.horizontal)
                    Spacer(minLength: 0)
                    if entry.images.isEmpty {
                        HStack(spacing: 4) {
                            Image(systemName: "train.side.front.car").scaleEffect(x: -1)
                            Image(systemName: "train.side.middle.car")
                            Image(systemName: "train.side.rear.car")
                            Spacer()
                            Text(first.firstLeg.service.uppercased()).font(NxtBuzFonts.caption).bold()
                        }.foregroundStyle(.yellow).padding(.horizontal)
                    } else {
                        RollingStockImageView(rollingStockImages: entry.images, maximumViewportWidth: geometry.size.width)
                    }
                }.padding(.vertical, 6)
            }
        } else {
            VStack(alignment: .leading, spacing: 6) {
                Label("Next trains", systemImage: "tram.fill").font(NxtBuzFonts.headline)
                Text(entry.message).font(NxtBuzFonts.subheadline)
            }.padding()
        }
    }
}

struct RichtingNextTrainWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "Richting.NextTrain", provider: NextTrainProvider()) { entry in
            RichtingTrainWidgetView(entry: entry).containerBackground(.background, for: .widget)
        }
        .configurationDisplayName("Next trains")
        .description("The next two rail journeys home from your nearest station, including connections.")
        .supportedFamilies([.systemMedium])
        .contentMarginsDisabled()
    }
}
