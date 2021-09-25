//
//  StarredBusArrivalsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import Foundation
import iosUmbrella

class StarredBusArrivalsViewModel : ObservableObject {
    @Published var screenState: StarredBusArrivalsScreenState = .Fetching
    
    func getArrivals() {
        self.screenState = .Fetching
        getArrivalsAct()
    }
    
    private func getArrivalsAct() {
        Di.get()
            .getStarredBusArrivalsUseCase()
            .getStarredBusArrivals { starredBusArrivalOutput in
                if let starredBusArrivalList = (starredBusArrivalOutput as? IosStarredBusArrivalOutput.Success)?.starredBusArrivalList {
                    debugPrint("yoyo: \(starredBusArrivalList.count)")
                    switch self.screenState {
                    case .Fetching, .Error:
                        let starredBusArrivalItemDataList = starredBusArrivalList.map { starredBusArrival in
                            StarredBusArrivalItemData(starredBusArrival: starredBusArrival)
                        }
                        
                        DispatchQueue.main.async {
                            self.screenState = .Success(
                                data: StarredBusArrivalsScreenSuccessData(
                                    starredBusArrivalItemDataList: starredBusArrivalItemDataList,
                                    lastUpdatedOn: Date()
                                )
                            )
                        }
                    case .Success(let data):
                        starredBusArrivalList.forEach { starredBusArrival in
                            let starredBusArrivalItemData = data.starredBusArrivalItemDataList.first { starredBusArrivalItemData in
                                starredBusArrivalItemData.starredBusArrival.busServiceNumber == starredBusArrival.busServiceNumber && starredBusArrivalItemData.starredBusArrival.busStopCode == starredBusArrival.busStopCode
                            }

                            DispatchQueue.main.sync {
                                if let starredBusArrivalItemData = starredBusArrivalItemData {
                                    starredBusArrivalItemData.starredBusArrival = starredBusArrival
                                } else {
                                    data.starredBusArrivalItemDataList.append(
                                        StarredBusArrivalItemData(starredBusArrival: starredBusArrival)
                                    )
                                }
                                data.lastUpdatedOnStr = BusStopArrivalsViewModel.getTime(date: Date())
                                data.outdatedResults = false
                            }
                        }
                        
                        DispatchQueue.main.sync {
                            var index = 0
                            while index >= 0 && index < data.starredBusArrivalItemDataList.count {
                                let starredBusArrivalItemData = data.starredBusArrivalItemDataList[index]
                                
                                let starredBusArrival = starredBusArrivalList.first { starredBusArrival in
                                    starredBusArrivalItemData.starredBusArrival.busServiceNumber == starredBusArrival.busServiceNumber && starredBusArrivalItemData.starredBusArrival.busStopCode == starredBusArrival.busStopCode
                                }
                                
                                if starredBusArrival == nil {
                                    data.starredBusArrivalItemDataList.remove(at: index)
                                    index -= 1
                                }
                                
                                index += 1
                            }
                            data.shouldShowList = !data.starredBusArrivalItemDataList.isEmpty
                            data.objectWillChange.send()
                        }
                    }
                    
                    debugPrint("\(self.screenState)")
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                        self.getArrivalsAct()
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
                            self.getArrivalsAct()
                        }
                    }
                }
            }
    }
}

enum StarredBusArrivalsScreenState {
    case Fetching
    case Error(message: String)
    case Success(data: StarredBusArrivalsScreenSuccessData)
}

class StarredBusArrivalsScreenSuccessData : ObservableObject {
    @Published var starredBusArrivalItemDataList: [StarredBusArrivalItemData]
    @Published var lastUpdatedOnStr: String
    var lastUpdatedOn: Date
    @Published var outdatedResults: Bool
    @Published var shouldShowList: Bool
    
    init(starredBusArrivalItemDataList: [StarredBusArrivalItemData], lastUpdatedOn: Date) {
        self.starredBusArrivalItemDataList = starredBusArrivalItemDataList
        self.lastUpdatedOn = lastUpdatedOn
        lastUpdatedOnStr = BusStopArrivalsViewModel.getTime(date: lastUpdatedOn)
        outdatedResults = false
        shouldShowList = !starredBusArrivalItemDataList.isEmpty
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
