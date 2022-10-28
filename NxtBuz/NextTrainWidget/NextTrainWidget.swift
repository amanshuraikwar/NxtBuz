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
        SimpleEntry(
            date: Date(),
            configuration: ConfigurationIntent(),
            sourceTrainStopName: "Amsterdam Centraal",
            destinationTrainStopName: "Utrecht Centraal",
            trainId: "973",
            trainType: "Intercity",
            departureFromSourceTime: "5:46 PM",
            arrivalAtDestinationTime: "6:13 PM",
            journeyDuration: "28 min",
            rollingStockImage: nil
        )
    }

    func getSnapshot(
        for configuration: ConfigurationIntent,
        in context: Context,
        completion: @escaping (SimpleEntry) -> ()
    ) {
        if context.isPreview {
            let entry = SimpleEntry(
                date: Date(),
                configuration: configuration,
                sourceTrainStopName: "Amsterdam Centraal",
                destinationTrainStopName: "Utrecht Centraal",
                trainId: "973",
                trainType: "Intercity",
                departureFromSourceTime: "5:46 PM",
                arrivalAtDestinationTime: "6:13 PM",
                journeyDuration: "28 min",
                rollingStockImage: nil
            )
            completion(entry)
        }
    }

    func getTimeline(
        for configuration: ConfigurationIntent,
        in context: Context,
        completion: @escaping (Timeline<Entry>) -> ()
    ) {
        Di.get().getTrainBetweenStopsUseCase().invoke1(
            fromTrainStopCode: "NS-API-TRAIN-ASDZ",
            toTrainStopCode: "NS-API-TRAIN-AMFS"
        ) { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(_):
                //completion(Timeline(entries: [], policy: .atEnd))
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
                            rollingStockImage: UIImage.init(data: data)
                        )
                        completion(Timeline(entries: [entry], policy: .atEnd))
                    }
                } catch { }
            case .Success(let trainDetailsList):
                DispatchQueue.global(qos: .background).async {
                    do {
                        NSLog("yoyo, \(trainDetailsList)")
                        let trainDetails = trainDetailsList[0] as! TrainDetails
                        let data = try Data(
                            contentsOf: URL(string: trainDetails.rollingStock[0].imageUrl!)!
                        )
                        DispatchQueue.main.async {
                            let entry = SimpleEntry(
                                date: Date(),
                                configuration: configuration,
                                sourceTrainStopName: trainDetails.sourceTrainStopName,
                                destinationTrainStopName: trainDetails.destinationTrainStopName,
                                trainId: trainDetails.trainCode,
                                trainType: trainDetails.trainCategoryName,
                                departureFromSourceTime: "5:46 PM",
                                arrivalAtDestinationTime: "6:13 PM",
                                journeyDuration: "28 min",
                                rollingStockImage: UIImage.init(data: data)
                            )
                            completion(Timeline(entries: [entry], policy: .atEnd))
                        }
                    } catch { }
                }
            }
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
    let rollingStockImage: UIImage?
}

struct NextTrainWidgetEntryView : View {
    var entry: Provider.Entry
    
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var nxtBuzTheme = NxtBuzTheme()
    
    var body: some View {
        GeometryReader { geometry in
            VStack(
                alignment: .leading,
                spacing: 0
            ) {
                HStack(alignment: .top, spacing: 0) {
                    Text(entry.sourceTrainStopName)
                        .font(NxtBuzFonts.body)
                        .fontWeight(.medium)
                    
                    Spacer()
                    
                    Text(entry.departureFromSourceTime)
                        .font(NxtBuzFonts.bodyMonospaced)
                        .fontWeight(.black)
                        .foregroundColor(Color(nxtBuzTheme.accentColor))
                }
                .padding(.trailing)
                .padding(.leading)
                .padding(.top)
                
                HStack(alignment: .top, spacing: 0) {
                    Image(systemName: "arrow.turn.down.right")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 12, height: 12)
                        .padding(.top, 4)
                    
                    Text(entry.destinationTrainStopName)
                        .font(NxtBuzFonts.callout)
                        .padding(.leading, 4)
                    
                    Spacer()
                    
                    Text(entry.arrivalAtDestinationTime)
                        .font(NxtBuzFonts.bodyMonospaced)
                        .fontWeight(.black)
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                }
                .padding(.trailing)
                .padding(.leading)
                .padding(.top, 2)
            
                Spacer()
                
                if let rollingStockImage = entry.rollingStockImage {
                    HStack(
                        spacing: 0
                    ) {
                        Spacer()
                        
                        Text(entry.trainId.uppercased())
                            .font(NxtBuzFonts.caption)
                            .fontWeight(.bold)
                            .padding(.horizontal, 2)
                            .padding(.vertical, 1)
                            .background(Color(nxtBuzTheme.accentColor))
                            .cornerRadius(4)
                            .foregroundColor(nxtBuzTheme.isDark ? Color(.systemGray6) : .white)
                            .padding(.trailing, 4)
                        
                        Text(entry.trainType.uppercased())
                            .font(NxtBuzFonts.caption)
                            .fontWeight(.bold)
                            .padding(.horizontal, 2)
                            .padding(.vertical, 1)
                            .background(Color(nxtBuzTheme.accentColor))
                            .cornerRadius(4)
                            .foregroundColor(nxtBuzTheme.isDark ? Color(.systemGray6) : .white)
                            .padding(.trailing)
                    }
                    Image(
                        uiImage: cropImage(
                            imageToCrop: rollingStockImage.imageScaledToSize(
                                toHeight: 40
                            )!,
                            width: geometry.size.width
                        )
                    )
                    .resizable()
                    .frame(width: geometry.size.width, height: 40)
                    .padding(.leading)
                    .padding(.bottom, 4)
                }
            }
        }
        .environmentObject(nxtBuzTheme)
        .onAppear {
            nxtBuzTheme.initTheme(isSystemInDarkMode: colorScheme == .dark)
        }
        .onChange(of: colorScheme) { colorScheme in
            nxtBuzTheme.onSystemThemeChanged(isDark: colorScheme == .dark)
        }
    }
    
    func cropImage(
        imageToCrop: UIImage,
        width: CGFloat
    ) -> UIImage{
        let scaleX = imageToCrop.size.width / CGFloat(imageToCrop.cgImage!.width)
        
        let rect = CGRect(
            x: 0,
            y: 0,
            width: Int(width / scaleX),
            height: imageToCrop.cgImage!.height
        )
        let imageRef = imageToCrop.cgImage!.cropping(to: rect)!
        let cropped = UIImage(cgImage: imageRef)
        return cropped
    }
}

extension UIImage {
        // returns a scaled version of the image
        func imageScaledToSize(
            toHeight height: CGFloat,
            isOpaque: Bool = false
        ) -> UIImage? {
            let size = CGSize(
                width: CGFloat(ceil(height * size.width / size.height)),
                height: height
            )
            
            // begin a context of the desired size
            UIGraphicsBeginImageContextWithOptions(size, isOpaque, 0.0)

            // draw image in the rect with zero origin and size of the context
            let imageRect = CGRect(origin: CGPointZero, size: size)
            self.draw(in: imageRect)

            // get the scaled image, close the context and return the image
            let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()

            return scaledImage
       }
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



extension Image {
    func centerCropped() -> some View {
        GeometryReader { geo in
            self
            .resizable()
            .scaledToFill()
            .frame(width: geo.size.width, height: geo.size.height)
            .clipped()
        }
    }
}

//struct NextTrainWidget_Previews: PreviewProvider {
//    static var previews: some View {
//        NextTrainWidgetEntryView(entry: SimpleEntry(date: Date(), configuration: ConfigurationIntent()))
//            .previewContext(WidgetPreviewContext(family: .systemSmall))
//    }
//}
