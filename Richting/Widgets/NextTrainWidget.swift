import ImageIO
import SwiftUI
import WidgetKit

struct TrainEntry: TimelineEntry {
    let date: Date
    let journeys: [Journey]
    let message: String
    var images: [URL: UIImage] = [:]
}

struct NextTrainProvider: TimelineProvider {
    func placeholder(in context: Context) -> TrainEntry {
        let journeys = [5, 20].map { minutes in
            let leg = JourneyLeg(from: "Amsterdam Centraal", to: "Utrecht Centraal",
                                 departure: .now.addingTimeInterval(Double(minutes * 60)),
                                 arrival: .now.addingTimeInterval(Double((minutes + 27) * 60)),
                                 service: "Intercity", platform: "5", number: "829", isWalk: false, imageURLs: [])
            return Journey(id: String(minutes), firstLeg: leg, onwardLegs: [], transfers: 0,
                           facilities: [])
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
                                      message: "Checking for later trains…", images: first.images))
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
                return TrainEntry(date: .now, journeys: [], message: "Your home station is already the nearest station.")
            }
            let trains = try await TrainService.shared.upcoming(from: from, to: to)
            let images = await stockImages(for: trains)
            let now = Date()
            let upcoming = trains.filter { $0.departureDate > now }
            return TrainEntry(date: now, journeys: upcoming,
                              message: "No rail journeys home from \(from.name).", images: images)
        } catch {
            return TrainEntry(date: .now, journeys: [], message: error.localizedDescription)
        }
    }
    // The second itinerary becomes the featured one after the first departure.
    private func stockImages(for journeys: [Journey]) async -> [URL: UIImage] {
        let urls = Set(journeys.flatMap(\.trainLegs).flatMap { $0.imageURLs.prefix(8) })
        return await withTaskGroup(of: (URL, UIImage?).self) { group in
            for url in urls where url.scheme == "https" {
                group.addTask {
                    let request = URLRequest(url: url, timeoutInterval: 2)
                    guard let (data, response) = try? await URLSession.shared.data(for: request),
                          let http = response as? HTTPURLResponse, (200..<300).contains(http.statusCode),
                          let source = CGImageSourceCreateWithData(data as CFData, nil) else { return (url, nil) }
                    let options = [kCGImageSourceCreateThumbnailFromImageAlways: true,
                                   kCGImageSourceThumbnailMaxPixelSize: 1200] as CFDictionary
                    let image = CGImageSourceCreateThumbnailAtIndex(source, 0, options)
                    return (url, image.map { UIImage(cgImage: $0) })
                }
            }
            var images: [URL: UIImage] = [:]
            for await (url, image) in group { images[url] = image }
            return images
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
                    TrainRoutePanel(from: first.from, to: "Home by",
                        times: entry.journeys.map { .init(departure: $0.departure, arrival: $0.arrival, platform: $0.firstLeg.platform, detail: $0.transferSummary) },
                        accent: accent, primary: Color(red: 0.15, green: 0.20, blue: 0.22),
                        secondary: dark ? Color(.lightGray) : Color(red: 0.27, green: 0.35, blue: 0.39), isDark: dark, splitFlapDepartures: true)
                    if first.transfers > 0 {
                        let legs = first.trainLegs
                        let changes = zip(legs, legs.dropFirst()).map { incoming, outgoing in
                            let minutes = Int(outgoing.departure.timeIntervalSince(incoming.arrival) / 60)
                            return "\(minutes) min at \(outgoing.from)"
                        }
                        Text(changes.joined(separator: " · "))
                            .lineLimit(1).minimumScaleFactor(0.75)
                            .font(NxtBuzFonts.caption).foregroundStyle(accent).padding(.horizontal)
                    } else {
                        Text(Journey.time(entry.date))
                            .font(NxtBuzFonts.caption).foregroundStyle(accent).padding(.horizontal)
                    }
                    Spacer(minLength: 0)
                    TrainArtworkRow(legs: first.trainLegs, images: entry.images, width: geometry.size.width)
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

private struct TrainArtworkRow: View {
    let legs: [JourneyLeg]
    let images: [URL: UIImage]
    let width: CGFloat

    var body: some View {
        let spacing: CGFloat = 4
        let count = max(legs.count, 1)
        let slotWidth = max(1, (width - spacing * CGFloat(count - 1)) / CGFloat(count))
        HStack(spacing: spacing) {
            ForEach(legs.indices, id: \.self) { index in
                let leg = legs[index]
                let coaches = leg.imageURLs.prefix(8).compactMap { images[$0] }
                Group {
                    if coaches.isEmpty {
                        HStack(spacing: 2) {
                            Image(systemName: "train.side.front.car").scaleEffect(x: -1)
                            Text(leg.service.uppercased()).font(NxtBuzFonts.caption).bold()
                                .lineLimit(1).minimumScaleFactor(0.65)
                        }.foregroundStyle(.yellow)
                    } else {
                        RollingStockImageView(rollingStockImages: coaches, maximumViewportWidth: slotWidth)
                    }
                }
                .frame(width: slotWidth, height: 40, alignment: .leading)
                .clipped()
                .shadow(color: .black.opacity(0.18), radius: 1, y: 1)
                .accessibilityElement(children: .ignore)
                .accessibilityLabel("Train \(index + 1), \(leg.service)")
            }
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
