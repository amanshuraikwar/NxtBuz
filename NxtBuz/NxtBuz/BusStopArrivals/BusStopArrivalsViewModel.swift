//
//  BusStopArrivalsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import Foundation
import iosUmbrella
import SwiftUI

class BusStopArrivalsViewModel : ObservableObject {
    @Published var screenState: BusStopArrivalsScreenState = .Fetching
    
    private var busStopCode: String? = nil
    private var uuid: UUID? = nil
    
    func getArrivals(busStopCode: String) {
        self.busStopCode = busStopCode
        let uuid = UUID()
        self.uuid = uuid
        getArrivalsAct(uuid: uuid)
    }
    
    private func getArrivalsAct(uuid: UUID) {
        debugPrint("yoyo", "\(uuid.uuidString) \(self.uuid?.uuidString ?? "")")
        if uuid != self.uuid {
            debugPrint("yoyo", "\(uuid.uuidString) \(self.uuid?.uuidString ?? "") mismatch!")
            return
        }
        
        if let busStopCode = self.busStopCode {
            Di.get()
                .getBusArrivalsUseCase()
                .invoke(busStopCode: busStopCode) { busStopArrivalOutput in
                    if let busStopArrivalList = (busStopArrivalOutput as? IosBusStopArrivalOutput.Success)?.busStopArrivalList {
                        switch self.screenState {
                        case .Fetching, .Error:
                            let busStopArrivalItemDataList = busStopArrivalList.map { busStopArrival in
                                BusStopArrivalItemData(
                                    busStopArrival: busStopArrival,
                                    starred: busStopArrival.starred
                                )
                            }
                            DispatchQueue.main.async {
                                self.screenState = .Success(
                                    data: BusStopArrivalScreenSuccessData(
                                        busStopArrivalItemDataList: busStopArrivalItemDataList,
                                        lastUpdatedOn: Date()
                                    )
                                )
                            }
                        case .Success(let data):
                            busStopArrivalList.forEach { busStopArrival in
                                let busStopArrivalItemData = data.busStopArrivalItemDataList.first { busStopArrivalItemData in
                                    busStopArrivalItemData.busStopArrival.busServiceNumber == busStopArrival.busServiceNumber
                                }

                                DispatchQueue.main.async {
                                    busStopArrivalItemData?.busStopArrival = busStopArrival
                                    data.lastUpdatedOnStr = BusStopArrivalsViewModel.getTime(date: Date())
                                    data.outdatedResults = false
                                }
                            }
                        }
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                            self.getArrivalsAct(uuid: uuid)
                        }   
                    }
                    
                    if let error = (busStopArrivalOutput as? IosBusStopArrivalOutput.Error) {
                        switch self.screenState {
                        case .Fetching, .Error:
                            DispatchQueue.main.async {
                                self.screenState = .Error(message: error.errorMessage)
                            }
                        case .Success(let data):
                            // if arrivals were last updated 5 mins ago
                            // mark the outdated
                            let diffs = Calendar.current.dateComponents([.minute], from: data.lastUpdatedOn, to: Date())
                            if let minutes = diffs.minute {
                                if minutes >= 5 {
                                    DispatchQueue.main.async {
                                        data.outdatedResults = true
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
        case .Success(let data):
            let busStopArrivalItemData = data.busStopArrivalItemDataList.first { busStopArrivalItemData in
                busStopArrivalItemData.busStopArrival.busServiceNumber == busServiceNumber
            }
            busStopArrivalItemData?.starred = newValue
            if let _ = busStopArrivalItemData, let busStopCode = busStopCode {
                Di.get().getToggleBusStopStarUseCase().invoke(
                    busStopCode: busStopCode,
                    busServiceNumber: busServiceNumber,
                    toggleTo: newValue
                )
            }
        default:
            break
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
    case Success(data: BusStopArrivalScreenSuccessData)
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
    @Published var busStopArrival: IosBusStopArrival
    @Published var starred: Bool
    
    init(busStopArrival: IosBusStopArrival, starred: Bool) {
        id = UUID()
        self.busStopArrival = busStopArrival
        self.starred = starred
    }
}
