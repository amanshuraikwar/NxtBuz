//
//  StarredBusArrivalsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import Foundation
import iosUmbrella
import UIKit
import WidgetKit

class StarredBusArrivalsViewModel : ObservableObject {
    @Published var screenState: StarredBusArrivalsScreenState = .Fetching
    private var uuid: UUID? = nil
    
    func getArrivals() {
        let uuid = UUID()
        self.uuid = uuid
        getArrivalsAct(uuid: uuid)
    }
    
    private func getArrivalsAct(uuid: UUID) {
        if uuid != self.uuid {
            return
        }
        
        Di.get()
            .getStarredBusArrivalsUseCase()
            .invoke { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(let message):
                    switch self.screenState {
                    case .Fetching, .Error:
                        Util.onMain {
                            self.screenState = .Error(message: message)
                        }
                    case .Success(let data):
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
                    let starredBusArrivalList = data.compactMap({ $0 as? StarredBusArrival })
                    switch self.screenState {
                    case .Fetching, .Error:
                        self.setSucessScreenState(starredBusArrivalList: starredBusArrivalList)
                    case .Success(let data):
                        self.updateSuccessScreenState(
                            data: data,
                            starredBusArrivalList: starredBusArrivalList
                        )
                    }
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                        self.getArrivalsAct(uuid: uuid)
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
                    busStop: starredBusArrivalItemDataList[0].starredBusArrival.busStop,
                    starredBusArrivalItemDataList: starredBusArrivalItemDataList
                )
            )
        }
        
        Util.onMain {
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
                        Util.onMain {
                            currentStarredBusArrivalItemData.starredBusArrival = newStarredBusArrival
                        }
                    } else {
                        Util.onMain {
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
                        busStop: newStarredBusArrivalList[0].busStop,
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
                        Util.onMain {
                            starredBusStop.starredBusArrivalItemDataList.remove(at: staredBusStopItemDataIndex)
                        }
                        staredBusStopItemDataIndex -= 1
                    }
                    
                    staredBusStopItemDataIndex += 1
                }
            } else {
                Util.onMain {
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
            data.outdatedResults = false
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
                completion: { result in
                    let useCaseResult = Util.toUseCaseResult(result)
                    switch useCaseResult {
                    case .Error(let message):
                        print(message)
                    case .Success(_):
                        WidgetCenter.shared.reloadTimelines(ofKind: "io.github.amanshuraikwar.NxtBuz.starredBusArrivalsWidget")
                        self.getArrivals()
                    }
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
    let busStop: BusStop
    @Published var starredBusArrivalItemDataList: [StarredBusArrivalItemData]
    
    init(
        busStopCode: String,
        busStop: BusStop,
        starredBusArrivalItemDataList: [StarredBusArrivalItemData]
    ) {
        self.busStopCode = busStopCode
        self.busStop = busStop
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
