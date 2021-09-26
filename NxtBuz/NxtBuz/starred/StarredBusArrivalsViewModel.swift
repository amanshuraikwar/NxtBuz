//
//  StarredBusArrivalsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import Foundation
import iosUmbrella
import UIKit

class StarredBusArrivalsViewModel : ObservableObject {
    @Published var screenState: StarredBusArrivalsScreenState = .Fetching
    private var uuid: UUID? = nil
    
    func getArrivals() {
        let uuid = UUID()
        self.uuid = uuid
        getArrivalsAct(uuid: uuid)
    }
    
    private func getArrivalsAct(uuid: UUID) {
        debugPrint("yoyo1", "\(uuid.uuidString) \(self.uuid?.uuidString ?? "")")
        if uuid != self.uuid {
            debugPrint("yoyo1", "\(uuid.uuidString) \(self.uuid?.uuidString ?? "") mismatch!")
            return
        }
        
        Di.get()
            .getStarredBusArrivalsUseCase()
            .getStarredBusArrivals { starredBusArrivalOutput in
                if let starredBusArrivalList = (starredBusArrivalOutput as? IosStarredBusArrivalOutput.Success)?.starredBusArrivalList {
                    
                    switch self.screenState {
                    case .Fetching, .Error:
                        self.setSucessScreenState(starredBusArrivalList: starredBusArrivalList)
                    case .Success(let data):
                        self.updateSuccessScreenState(
                            data: data,
                            starredBusArrivalList: starredBusArrivalList
                        )
                    }
                    
                    debugPrint("\(self.screenState)")
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                        self.getArrivalsAct(uuid: uuid)
                    }
                }
                
                if let error = (starredBusArrivalOutput as? IosStarredBusArrivalOutput.Error) {
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
    
    private func setSucessScreenState(starredBusArrivalList: [StarredBusArrival]) {
        let starredBusArrivalItemDataList = starredBusArrivalList.map { starredBusArrival in
            StarredBusArrivalItemData(starredBusArrival: starredBusArrival)
        }
        
        let busStopCodeArrivalItemDataDict = Dictionary(
            grouping: starredBusArrivalItemDataList,
            by: { $0.starredBusArrival.busStopCode }
        )
        
        var starredBusStopList: [StarredBusStop] = []
        
        busStopCodeArrivalItemDataDict.forEach { busStopCode, starredBusArrivalItemDataList in
            starredBusStopList.append(
                StarredBusStop(
                    busStopCode: busStopCode,
                    busStopDescription: starredBusArrivalItemDataList[0].starredBusArrival.busStopDescription,
                    starredBusArrivalItemDataList: starredBusArrivalItemDataList
                )
            )
        }
        
        DispatchQueue.main.sync {
            self.screenState = .Success(
                data: StarredBusArrivalsScreenSuccessData(
                    starredBusStopList: starredBusStopList,
                    lastUpdatedOn: Date()
                )
            )
        }
    }
    
    private func updateSuccessScreenState(
        data: StarredBusArrivalsScreenSuccessData,
        starredBusArrivalList incomingStarredBusArrivalList: [StarredBusArrival]
    ) {
        // todo: remove
        data.starredBusStopList.forEach { starredBusStop in
            print("yoyo1 before \(starredBusStop.busStopDescription)")
            starredBusStop.starredBusArrivalItemDataList.forEach { starredBusArrivalItemData in
                print("yoyo1 before \(starredBusArrivalItemData.starredBusArrival.busServiceNumber)")
            }
        }
        
        let busStopCodeArrivalDict = Dictionary(
            grouping: incomingStarredBusArrivalList,
            by: { $0.busStopCode }
        )
        
        var starredBusStopListToAdd: [StarredBusStop] = []
        
        busStopCodeArrivalDict.forEach { busStopCode, newStarredBusArrivalList in
            let currentStarredBusStop =
            data
                .starredBusStopList
                .first { starredBusStop in
                    starredBusStop.busStopCode == busStopCode
                }
             
            if let currentStarredBusStop = currentStarredBusStop {
                newStarredBusArrivalList.forEach { newStarredBusArrival in
                    let currentStarredBusArrivalItemData =
                    currentStarredBusStop
                        .starredBusArrivalItemDataList
                        .first { starredBusArrivalItemData in
                            starredBusArrivalItemData.starredBusArrival.busServiceNumber == newStarredBusArrival.busServiceNumber
                    }
                    
                    if let currentStarredBusArrivalItemData = currentStarredBusArrivalItemData {
                        DispatchQueue.main.sync {
                            currentStarredBusArrivalItemData.starredBusArrival = newStarredBusArrival
                        }
                    } else {
                        DispatchQueue.main.sync {
                            currentStarredBusStop.starredBusArrivalItemDataList.append(
                                StarredBusArrivalItemData(
                                    starredBusArrival: newStarredBusArrival
                                )
                            )
                        }
                    }
                }
            } else {
                starredBusStopListToAdd.append(
                    StarredBusStop(
                        busStopCode: busStopCode,
                        busStopDescription: newStarredBusArrivalList[0].busStopDescription,
                        starredBusArrivalItemDataList: newStarredBusArrivalList.map { starredBusArrival in
                            StarredBusArrivalItemData(starredBusArrival: starredBusArrival)
                        }
                    )
                )
            }
        }
        
        var starredBusStopIndex = 0
        while (starredBusStopIndex >= 0 && starredBusStopIndex < data.starredBusStopList.count) {
            let starredBusStop = data.starredBusStopList[starredBusStopIndex]
            let newStarredBusArrivalList = busStopCodeArrivalDict[starredBusStop.busStopCode]
            if let newStarredBusArrivalList = newStarredBusArrivalList {
                var staredBusStopItemDataIndex = 0
                while (
                    staredBusStopItemDataIndex >= 0 && staredBusStopItemDataIndex <
                    starredBusStop.starredBusArrivalItemDataList.count
                ) {
                    let currentStarredBusArrivalItemData = starredBusStop.starredBusArrivalItemDataList[staredBusStopItemDataIndex]
                    let contains = newStarredBusArrivalList.contains { starredBusArrival in
                        starredBusArrival.busServiceNumber == currentStarredBusArrivalItemData.starredBusArrival.busServiceNumber
                    }
                    
                    if !contains {
                        DispatchQueue.main.sync {
                            starredBusStop.starredBusArrivalItemDataList.remove(at: staredBusStopItemDataIndex)
                        }
                        staredBusStopItemDataIndex -= 1
                    }
                    
                    staredBusStopItemDataIndex += 1
                }
            } else {
                DispatchQueue.main.sync {
                    data.starredBusStopList.remove(at: starredBusStopIndex)
                }
                starredBusStopIndex -= 1
            }
            starredBusStopIndex += 1
        }
        
        DispatchQueue.main.sync {
            data.starredBusStopList.append(contentsOf: starredBusStopListToAdd)
            data.lastUpdatedOn = Date()
            data.lastUpdatedOnStr = BusStopArrivalsViewModel.getTime(date: data.lastUpdatedOn)
            data.shouldShowList = !data.starredBusStopList.isEmpty
            
            print("yoyo1 \(data.starredBusStopList.count)")
            data.starredBusStopList.forEach { starredBusStop in
                print("yoyo1 after \(starredBusStop.busStopDescription)")
                starredBusStop.starredBusArrivalItemDataList.forEach { starredBusArrivalItemData in
                    print("yoyo1 after \(starredBusArrivalItemData.starredBusArrival.busServiceNumber)")
                }
            }
        }
    }
    
    func onRetryClick() {
        if self.uuid != nil {
            self.screenState = .Fetching
            getArrivals()
        }
    }
    
    func stopArrivalsLoop() {
        self.uuid = nil
    }
    
    func onUnStarClick(busStopCode: String, busServiceNumber: String) {
        Di.get()
            .getToggleBusStopStarUseCase()
            .invoke(
                busStopCode: busStopCode,
                busServiceNumber: busServiceNumber,
                toggleTo: false,
                completion: {
                    self.getArrivals()
                }
            )
    }
}

enum StarredBusArrivalsScreenState {
    case Fetching
    case Error(message: String)
    case Success(data: StarredBusArrivalsScreenSuccessData)
}

class StarredBusArrivalsScreenSuccessData : ObservableObject {
    @Published var starredBusStopList: [StarredBusStop]
    @Published var lastUpdatedOnStr: String
    var lastUpdatedOn: Date
    @Published var outdatedResults: Bool
    @Published var shouldShowList: Bool
    
    init(starredBusStopList: [StarredBusStop], lastUpdatedOn: Date) {
        self.starredBusStopList = starredBusStopList
        self.lastUpdatedOn = lastUpdatedOn
        lastUpdatedOnStr = BusStopArrivalsViewModel.getTime(date: lastUpdatedOn)
        outdatedResults = false
        shouldShowList = !starredBusStopList.isEmpty
    }
}

class StarredBusStop : ObservableObject, Identifiable {
    var id: UUID = UUID()
    let busStopCode: String
    let busStopDescription: String
    @Published var starredBusArrivalItemDataList: [StarredBusArrivalItemData]
    
    init(
        busStopCode: String,
        busStopDescription: String,
        starredBusArrivalItemDataList: [StarredBusArrivalItemData]
    ) {
        self.busStopCode = busStopCode
        self.busStopDescription = busStopDescription
        self.starredBusArrivalItemDataList = starredBusArrivalItemDataList
    }
}

class StarredBusArrivalItemData : ObservableObject, Identifiable {
    var id: UUID?
    @Published var starredBusArrival: StarredBusArrival
    
    init(starredBusArrival: StarredBusArrival) {
        id = UUID()
        self.starredBusArrival = starredBusArrival
    }
}
