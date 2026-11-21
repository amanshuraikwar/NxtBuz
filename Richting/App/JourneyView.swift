import SwiftUI

struct JourneyView: View {
    let model: JourneyViewModel
    @State private var pickingHome = false
    @Environment(\.scenePhase) private var phase

    var body: some View {
        NavigationStack {
            List {
                Section("Journey home") {
                    Button { pickingHome = true } label: {
                        LabeledContent("Home station", value: model.home?.name ?? "Choose station")
                    }
                    .accessibilityIdentifier("homeStationSelection")
                    .disabled(model.stations.isEmpty)
                    if let nearest = model.nearest {
                        LabeledContent("Nearest station", value: nearest.name)
                    }
                    if let message = model.catalogError {
                        Text(message)
                        Button("Retry") { Task { await model.loadStations() } }
                    }
                    if model.home != nil {
                        Button("Find trains near me", systemImage: "location") {
                            Task { await model.refresh(requestPermission: true) }
                        }.disabled(model.state == .loading || model.state == .locating)
                    }
                }
                switch model.state {
                case .idle: EmptyView()
                case .locating: ProgressView("Finding your nearest station…")
                case .loading: ProgressView("Checking departures…")
                case .atHome: Text("Your home station is already the nearest station.")
                case .noTrain: Text("No upcoming rail journeys home from this station.")
                case .failed(let message): Text(message)
                case .loaded(let trains):
                    Section("Next trains home") {
                        ForEach(trains) { journey in
                            VStack(alignment: .leading, spacing: 12) {
                                JourneyCard(journey: journey)
                                Button("Track on Lock Screen", systemImage: "livephoto") {
                                    Task { await model.track(journey) }
                                }.buttonStyle(.borderless)
                            }.padding(.vertical, 4)
                        }
                        if let message = model.activityMessage { Text(message).font(.footnote) }
                    }
                }
                Section("Home Screen widget") {
                    Label("Add Next trains to your Home Screen. It uses your location and home station automatically.",
                          systemImage: "rectangle.grid.2x2").font(.subheadline)
                    NavigationLink("Station artwork credits") {
                        List {
                            Section("Abcoude") {
                                Text("[Photograph by T Houdijk](https://commons.wikimedia.org/wiki/File:Station_Abcoude_3.jpg)")
                                Text("[CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/)")
                            }
                            Section("Amsterdam Centraal") {
                                Text("[Photograph by Diego Delso](https://commons.wikimedia.org/wiki/File:Estaci%C3%B3n_Central,_%C3%81msterdam,_Pa%C3%ADses_Bajos,_2016-05-30,_DD_01-03_HDR.jpg)")
                                Text("[CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)")
                            }
                            Text("Richting adapted each photograph with ink colours, grain, and a transparent fade. Each adaptation uses the licence of its source photograph.")
                                .font(.footnote)
                        }
                        .navigationTitle("Station artwork credits")
                    }
                }
            }
            .navigationTitle("Richting")
            .task {
                await model.loadStations()
                if model.home != nil { await model.refresh(requestPermission: false) }
            }
            .onChange(of: phase) { previous, current in
                if previous == .background && current == .active {
                    Task { await model.refresh(requestPermission: false) }
                }
            }
            .sheet(isPresented: $pickingHome) {
                StationPicker(title: "Home station", stations: model.stations) { station in
                    model.setHome(station)
                    pickingHome = false
                    Task { await model.refresh(requestPermission: true) }
                }
            }
        }
    }
}

private struct StationPicker: View {
    let title: String
    let stations: [Station]
    let select: (Station) -> Void
    @State private var query = ""
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            let matches = stations.filter { $0.hasDepartures && $0.matches(query) }
            List(matches) { station in
                Button(station.name) { select(station) }
            }
            .overlay { if matches.isEmpty { ContentUnavailableView.search(text: query) } }
            .searchable(text: $query, prompt: "Station name or code")
            .navigationTitle(title)
            .toolbar { Button("Cancel") { dismiss() } }
        }
    }
}
