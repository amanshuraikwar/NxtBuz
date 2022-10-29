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
        for configuration: ConfigurationIntent,
        in context: Context,
        completion: @escaping (SimpleEntry) -> ()
    ) {
        if context.isPreview {
            completion(getDemoEntry())
        }
    }

    func getTimeline(
        for configuration: ConfigurationIntent,
        in context: Context,
        completion: @escaping (Timeline<SimpleEntry>) -> ()
    ) {
        Di.get().getTrainBetweenStopsUseCase().invoke1(
            fromTrainStopCode: "NS-API-TRAIN-ASD",
            toTrainStopCode: "NS-API-TRAIN-ALM"
        ) { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(_):
                do {
                    let data = try Data(
                        contentsOf: URL(string: "https://vt.ns-mlab.nl/v1/images/virm_4.png")!
                    )
                    DispatchQueue.main.async {
                        let entry = SimpleEntry(
                            date: Date(),
                            configuration: configuration,
                            sourceTrainStopName: "ERROR",
                            destinationTrainStopName: "ERROR",
                            trainId: "973",
                            trainType: "Intercity",
                            departureFromSourceTime: "5:46 PM",
                            arrivalAtDestinationTime: "6:13 PM",
                            journeyDuration: "28 min",
                            rollingStockImages: [UIImage.init(data: data)!],
                            facilities: [TrainFacility.bicycle]
                        )
                        completion(Timeline(entries: [entry], policy: .atEnd))
                    }
                } catch { }
            case .Success(let trainDetails):
                DispatchQueue.global(qos: .background).async {
                    do {
                        NSLog("yoyo, \(trainDetails)")
                        let trainDetails = trainDetails as NextTrainBetweenStopsDetails
                        let rollingStockImages = try trainDetails.rollingStockImages.map { image in
                            UIImage.init(
                                data: try Data(
                                    contentsOf: URL(string: image)!
                                )
                            )!
                        }
                        
                        DispatchQueue.main.async {
                            let entry = SimpleEntry(
                                date: Date(),
                                configuration: configuration,
                                sourceTrainStopName: trainDetails.fromTrainStopName,
                                destinationTrainStopName: trainDetails.toTrainStopName,
                                trainId: trainDetails.trainCode,
                                trainType: trainDetails.trainCategoryName,
                                departureFromSourceTime: trainDetails.departureFromIntendedSource,
                                arrivalAtDestinationTime: trainDetails.arrivalAtIntendedDestination,
                                journeyDuration: "--",
                                rollingStockImages: rollingStockImages,
                                facilities: trainDetails.facilities
                            )
                            completion(Timeline(entries: [entry], policy: .atEnd))
                        }
                    } catch { }
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
                configuration: ConfigurationIntent(),
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
                facilities: [TrainFacility.bicycle]
            )
        } catch {
            return SimpleEntry(
                date: Date(),
                configuration: ConfigurationIntent(),
                sourceTrainStopName: "Amsterdam Centraal",
                destinationTrainStopName: "Amersfoort Centraal",
                trainId: "1529",
                trainType: "NS Intercity Direct",
                departureFromSourceTime: "09:01",
                arrivalAtDestinationTime: "09:34",
                journeyDuration: "--",
                rollingStockImages: [],
                facilities: [TrainFacility.bicycle]
            )
        }
    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let configuration: ConfigurationIntent
    let sourceTrainStopName: String
    let destinationTrainStopName: String
    let trainId: String
    let trainType: String
    let departureFromSourceTime: String
    let arrivalAtDestinationTime: String
    let journeyDuration: String
    let rollingStockImages: [UIImage]
    let facilities: [TrainFacility]
}

@main
struct NextTrainWidget: Widget {
    let kind: String = "io.github.amanshuraikwar.NxtBuz.NextTrainWidget"

    var body: some WidgetConfiguration {
        IntentConfiguration(
            kind: kind,
            intent: ConfigurationIntent.self,
            provider: Provider()
        ) { entry in
            NextTrainWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Next train")
        .description("Displays next train between two stations.")
        .supportedFamilies([.systemMedium])
    }
}
