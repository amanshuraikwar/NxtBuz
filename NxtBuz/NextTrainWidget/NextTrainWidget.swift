//
//  NextTrainWidget.swift
//  NextTrainWidget
//
//  Created by Amanshu Raikwar on 17/10/22.
//

import WidgetKit
import SwiftUI
import Intents
import iosUmbrella

struct Provider: IntentTimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        return getDemoEntry()
    }

    func getSnapshot(
        for configuration: NextTrainWidgetConfigurationIntent,
        in context: Context,
        completion: @escaping (SimpleEntry) -> ()
    ) {
        if context.isPreview {
            completion(getDemoEntry())
        }
    }

    func getTimeline(
        for configuration: NextTrainWidgetConfigurationIntent,
        in context: Context,
        completion: @escaping (Timeline<SimpleEntry>) -> ()
    ) {
        let fromTrainStopCode = configuration.FromTrainStop?.trainStopCode
        let toTrainStopCode = configuration.ToTrainStop?.trainStopCode
        let fromTrainStopName = configuration.FromTrainStop?.trainStopName
        let toTrainStopName = configuration.ToTrainStop?.trainStopName
        
        if fromTrainStopCode != nil
            && toTrainStopCode != nil
            && fromTrainStopName != nil
            && toTrainStopName != nil {
            fetchTrains(
                configuration: configuration,
                fromTrainStopCode: fromTrainStopCode!,
                fromTrainStopName: fromTrainStopName!,
                toTrainStopCode: toTrainStopCode!,
                toTrainStopName: toTrainStopName!,
                completion: completion
            )
        } else {
            DispatchQueue.main.async {
                let entry = SimpleEntry(
                    date: Date(),
                    configuration: configuration,
                    sourceTrainStopName: fromTrainStopName ?? "Not selected",
                    destinationTrainStopName: toTrainStopName ?? "Not selected",
                    trainId: "",
                    trainType: "Please configure the widget",
                    departureFromSourceTime: "--",
                    arrivalAtDestinationTime: "--",
                    journeyDuration: "--",
                    rollingStockImages: [],
                    facilities: [],
                    sourceTrainStopTrack: nil
                )
                completion(Timeline(entries: [entry], policy: .atEnd))
            }
        }
    }
    
    private func fetchTrains(
        configuration: NextTrainWidgetConfigurationIntent,
        fromTrainStopCode: String,
        fromTrainStopName: String,
        toTrainStopCode: String,
        toTrainStopName: String,
        completion: @escaping (Timeline<SimpleEntry>) -> ()
    ) {
        Di.get().getTrainBetweenStopsUseCase().invoke1(
            fromTrainStopCode: fromTrainStopCode,
            toTrainStopCode: toTrainStopCode
        ) { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(let message):
                DispatchQueue.main.async {
                    let entry = SimpleEntry(
                        date: Date(),
                        configuration: configuration,
                        sourceTrainStopName: fromTrainStopName,
                        destinationTrainStopName: toTrainStopName,
                        trainId: "",
                        trainType: message,
                        departureFromSourceTime: "--",
                        arrivalAtDestinationTime: "--",
                        journeyDuration: "--",
                        rollingStockImages: [],
                        facilities: [],
                        sourceTrainStopTrack: nil
                    )
                    completion(Timeline(entries: [entry], policy: .atEnd))
                }
            case .Success(let output):
                DispatchQueue.global(qos: .background).async {
                    if let output = output as? NextTrainBetweenStopsOutput.TrainFound {
                        let trainDetails = output.details
                        
                        let rollingStockImages: [UIImage]
                        
                        do {
                            rollingStockImages = try trainDetails.rollingStockImages.map { image in
                                UIImage.init(
                                    data: try Data(
                                        contentsOf: URL(string: image)!
                                    )
                                )!
                            }
                        } catch {
                            rollingStockImages = []
                        }
                                                        
                        DispatchQueue.main.async {
                            let entry = SimpleEntry(
                                date: Date(),
                                configuration: configuration,
                                sourceTrainStopName: trainDetails.fromTrainStopName,
                                destinationTrainStopName: trainDetails.toTrainStopName,
                                trainId: trainDetails.trainCode,
                                trainType:
                                    trainDetails.trainCategoryName ?? trainDetails.stopsToTravel,
                                departureFromSourceTime: trainDetails.departureFromIntendedSource,
                                arrivalAtDestinationTime: trainDetails.arrivalAtIntendedDestination,
                                journeyDuration: "--",
                                rollingStockImages: rollingStockImages,
                                facilities: trainDetails.facilities,
                                sourceTrainStopTrack: trainDetails.fromTrainStopTrack
                            )
                            completion(Timeline(entries: [entry], policy: .atEnd))
                        }
                    }
                        
                    if let _ = output as? NextTrainBetweenStopsOutput.NoTrainFound {
                        DispatchQueue.main.async {
                            let entry = SimpleEntry(
                                date: Date(),
                                configuration: configuration,
                                sourceTrainStopName: fromTrainStopName,
                                destinationTrainStopName: toTrainStopName,
                                trainId: "",
                                trainType: "NO TRAIN FOUND",
                                departureFromSourceTime: "--",
                                arrivalAtDestinationTime: "--",
                                journeyDuration: "--",
                                rollingStockImages: [],
                                facilities: [],
                                sourceTrainStopTrack: nil
                            )
                            completion(Timeline(entries: [entry], policy: .atEnd))
                        }
                    }
                    
                    if let _ = output as? NextTrainBetweenStopsOutput.TrainStopsAreSame {
                        DispatchQueue.main.async {
                            let entry = SimpleEntry(
                                date: Date(),
                                configuration: configuration,
                                sourceTrainStopName: fromTrainStopName,
                                destinationTrainStopName: toTrainStopName,
                                trainId: "",
                                trainType: "STOPS SHOULD BE DIFFERENT",
                                departureFromSourceTime: "--",
                                arrivalAtDestinationTime: "--",
                                journeyDuration: "--",
                                rollingStockImages: [],
                                facilities: [],
                                sourceTrainStopTrack: nil
                            )
                            completion(Timeline(entries: [entry], policy: .atEnd))
                        }
                    }
                }
            }
        }
    }
    
    private func getDemoEntry() -> SimpleEntry {
        do {
            let engine = try Data(
                contentsOf: URL(string: "https://vt.ns-mlab.nl/v1/images/e-loc_tr25.png")!
            )
            let coaches = try Data(
                contentsOf: URL(string: "https://vt.ns-mlab.nl/v1/images/icr_7.png")!
            )
            return SimpleEntry(
                date: Date(),
                configuration: NextTrainWidgetConfigurationIntent(),
                sourceTrainStopName: "Amsterdam Centraal",
                destinationTrainStopName: "Amersfoort Centraal",
                trainId: "1529",
                trainType: "NS Intercity Direct",
                departureFromSourceTime: "09:01",
                arrivalAtDestinationTime: "09:34",
                journeyDuration: "--",
                rollingStockImages: [
                    UIImage.init(data: engine)!,
                    UIImage.init(data: coaches)!
                ],
                facilities: [TrainFacility.powerSockets, TrainFacility.bicycle, TrainFacility.wifi],
                sourceTrainStopTrack: "10b"
            )
        } catch {
            return SimpleEntry(
                date: Date(),
                configuration: NextTrainWidgetConfigurationIntent(),
                sourceTrainStopName: "Amsterdam Centraal",
                destinationTrainStopName: "Amersfoort Centraal",
                trainId: "1529",
                trainType: "NS Intercity Direct",
                departureFromSourceTime: "09:01",
                arrivalAtDestinationTime: "09:34",
                journeyDuration: "--",
                rollingStockImages: [],
                facilities: [TrainFacility.powerSockets, TrainFacility.bicycle, TrainFacility.wifi],
                sourceTrainStopTrack: "3"
            )
        }
    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let configuration: NextTrainWidgetConfigurationIntent
    let sourceTrainStopName: String
    let destinationTrainStopName: String
    let trainId: String
    let trainType: String
    let departureFromSourceTime: String
    let arrivalAtDestinationTime: String
    let journeyDuration: String
    let rollingStockImages: [UIImage]
    let facilities: [TrainFacility]
    let sourceTrainStopTrack: String?
}

struct NextTrainWidget: Widget {
    let kind: String = "io.github.amanshuraikwar.NxtBuz.NextTrainWidget"

    var body: some WidgetConfiguration {
        IntentConfiguration(
            kind: kind,
            intent: NextTrainWidgetConfigurationIntent.self,
            provider: Provider()
        ) { entry in
            NextTrainWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Next train")
        .description("Displays next train between two stations.")
        .supportedFamilies([.systemMedium])
    }
}
