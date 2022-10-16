//
//  BusStopArrivalsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import Foundation
import iosUmbrella
import SwiftUI
import WidgetKit

class BusStopArrivalsViewModel : ObservableObject {
    @Published var screenState: BusStopArrivalsScreenState = .Fetching
    
    private var busStopCode: String? = nil
    private var uuid: UUID? = nil
    private var busStop: BusStop? = nil
    
    @Published var busStopDescription = ""
    @Published var busStopRoadName = ""
    
    func getArrivals(busStopCode: String) {
        if self.busStopCode == busStopCode {
            if case BusStopArrivalsScreenState.Success = screenState {
                self.busStopCode = busStopCode
                let uuid = UUID()
                self.uuid = uuid
                getArrivalsAct(uuid: uuid)
                return
            }
        }
        
        Di.get()
            .getBusStopUseCase()
            .invoke(busStopCode: busStopCode) { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(_):
                    Util.onMain {
                        self.screenState = .Error(message: "Count not fetch bus stop details, please try again.")
                    }
                case .Success(let data):
                    let busStop = data
                    Util.onMain {
                        self.busStop = busStop
                        self.busStopCode = busStop.code
                        let uuid = UUID()
                        self.uuid = uuid
                        self.busStopDescription = busStop.description_
                        self.busStopRoadName = busStop.roadName
                        self.getArrivalsAct(uuid: uuid)
                    }
                }
            }
    }
    
    private func getArrivalsAct(uuid: UUID) {
        if uuid != self.uuid {
            return
        }
        
        if let busStopCode = self.busStopCode {
            Di.get()
                .getBusArrivalsUseCase()
                .invoke(busStopCode: busStopCode) {  result in
                    let useCaseResult = Util.toUseCaseResult(result)
                    switch useCaseResult {
                    case .Error(let message):
                        switch self.screenState {
                        case .Fetching, .Error:
                            Util.onMain {
                                self.screenState = .Error(message: message)
                            }
                        case .Success(_, let data):
                            // if arrivals were last updated 5 mins ago
                            // mark the outdated
                            let diffs = Calendar.current.dateComponents([.minute], from: data.lastUpdatedOn, to: Date())
                            if let minutes = diffs.minute {
                                if minutes >= 2 {
                                    DispatchQueue.main.async {
                                        data.outdatedResults = true
                                    }
                                }
                            }
                            DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                                self.getArrivalsAct(uuid: uuid)
                            }
                        }
                    case .Success(let data):
                        let busStopArrivalResultList = data.compactMap({ $0 as? BusStopArrivalResult })
                        switch self.screenState {
                        case .Fetching, .Error:
                            let busStopArrivalItemDataList = busStopArrivalResultList.map { busStopArrivalResult in
                                BusStopArrivalItemData(
                                    busStopArrival: busStopArrivalResult.busStopArrival,
                                    starred: busStopArrivalResult.isStarred
                                )
                            }
                            Util.onMain {
                                self.screenState = .Success(
                                    busStopCode: busStopCode,
                                    data: BusStopArrivalScreenSuccessData(
                                        busStopArrivalItemDataList: busStopArrivalItemDataList,
                                        lastUpdatedOn: Date()
                                    )
                                )
                            }
                        case .Success(_, let data):
                            busStopArrivalResultList.forEach { busStopArrivalResult in
                                let busStopArrivalItemData = data.busStopArrivalItemDataList.first { busStopArrivalItemData in
                                    busStopArrivalItemData.busStopArrival.busServiceNumber == busStopArrivalResult.busStopArrival.busServiceNumber
                                }

                                Util.onMain {
                                    busStopArrivalItemData?.busStopArrival = busStopArrivalResult.busStopArrival
                                    busStopArrivalItemData?.starred = busStopArrivalResult.isStarred
                                    data.lastUpdatedOn = Date()
                                    data.lastUpdatedOnStr = BusStopArrivalsViewModel.getTime(date: data.lastUpdatedOn)
                                    data.outdatedResults = false
                                }
                            }
                        }
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                            self.getArrivalsAct(uuid: uuid)
                        }
                    }
                }
        }
    }
    
    func onRetryClick() {
        if let busStopCode = self.busStopCode {
            self.screenState = .Fetching
            getArrivals(busStopCode: busStopCode)
        }
    }
    
    func stopArrivalsLoop() {
        self.busStopCode = nil
        self.uuid = nil
    }
    
    func onStarToggle(busServiceNumber: String, newValue: Bool) {
        switch self.screenState {
        case .Success(_, let data):
            let busStopArrivalItemData = data.busStopArrivalItemDataList.first { busStopArrivalItemData in
                busStopArrivalItemData.busStopArrival.busServiceNumber == busServiceNumber
            }
            busStopArrivalItemData?.starred = newValue
            if let _ = busStopArrivalItemData, let busStopCode = busStopCode {
                Di.get().getToggleBusStopStarUseCase().invoke(
                    busStopCode: busStopCode,
                    busServiceNumber: busServiceNumber,
                    toggleTo: newValue,
                    completion: { result in
                        let useCaseResult = Util.toUseCaseResult(result)
                        switch useCaseResult {
                        case .Success(_):
                            WidgetCenter.shared.reloadTimelines(ofKind: "io.github.amanshuraikwar.NxtBuz.starredBusArrivalsWidget")
                        case .Error(let message):
                            print("Star toggle failed \(message).")
                        }
                    }
                )
            }
        default:
            break
        }
    }
    
    func onNavigateClick() {
        if let busStop = busStop {
            if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
                UIApplication.shared.open(
                    NSURL(string: "comgooglemaps://?saddr=&daddr=\(Float(busStop.latitude)),\(Float(busStop.longitude))&directionsmode=walking")! as URL
                )
            } else if (UIApplication.shared.canOpenURL(URL(string:"maps://")!)) {
                UIApplication.shared.open(
                    NSURL(string: "maps://?saddr=&daddr=\(Float(busStop.latitude)),\(Float(busStop.longitude))&directionsmode=walking")! as URL
                )
            }
        }
    }
    
    public static func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}

enum BusStopArrivalsScreenState {
    case Fetching
    case Error(message: String)
    case Success(
        busStopCode: String,
        data: BusStopArrivalScreenSuccessData
    )
}

class BusStopArrivalScreenSuccessData : ObservableObject {
    @Published var busStopArrivalItemDataList: [BusStopArrivalItemData]
    @Published var lastUpdatedOnStr: String
    var lastUpdatedOn: Date
    @Published var outdatedResults: Bool
    
    init(busStopArrivalItemDataList: [BusStopArrivalItemData], lastUpdatedOn: Date) {
        self.busStopArrivalItemDataList = busStopArrivalItemDataList
        self.lastUpdatedOn = lastUpdatedOn
        lastUpdatedOnStr = BusStopArrivalsViewModel.getTime(date: lastUpdatedOn)
        outdatedResults = false
    }
}

class BusStopArrivalItemData : ObservableObject, Identifiable {
    var id: UUID?
    @Published var busStopArrival: BusStopArrival
    @Published var starred: Bool
    
    init(busStopArrival: BusStopArrival, starred: Bool) {
        id = UUID()
        self.busStopArrival = busStopArrival
        self.starred = starred
    }
}
