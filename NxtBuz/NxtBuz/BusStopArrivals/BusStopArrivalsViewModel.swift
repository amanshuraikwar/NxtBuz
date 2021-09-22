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
    @Published var lastUpdatedOn: String = "NONO"
    
    private var busStopArrivalsLoop: BusStopArrivalsLoop?
    private var busStopCode: String? = nil
    
    func getArrivals(busStopCode: String) {
        self.busStopCode = busStopCode
        getArrivalsAct()
    }
    
    private func getArrivalsAct() {
        if let busStopCode = self.busStopCode {
            Di.get()
                .getBusArrivalsUseCase()
                .invoke(busStopCode: busStopCode) { busStopArrivalList in
                    switch self.screenState {
                        case .Fetching:
                            let busStopArrivalItemDataList = busStopArrivalList.map { busStopArrival in
                                BusStopArrivalItemData(busStopArrival: busStopArrival)
                            }
                            DispatchQueue.main.async {
                                self.screenState = .Success(busStopArrivalItemDataList: busStopArrivalItemDataList, lastUpdatedOn: self.getTime())
                            }
                        case .Success(let busStopArrivalItemDataList, _):
                            busStopArrivalList.forEach { busStopArrival in
                                let busStopArrivalItemData = busStopArrivalItemDataList.first { busStopArrivalItemData in
                                    busStopArrivalItemData.busStopArrival.busServiceNumber == busStopArrival.busServiceNumber
                                }
    
                                DispatchQueue.main.async {
                                    busStopArrivalItemData?.busStopArrival = busStopArrival
                                    busStopArrivalItemData?.lastUpdatedOn = self.getTime()
                                }
                            }
                    }
                    DispatchQueue.main.async {
                        self.lastUpdatedOn = self.getTime()
                    }
                    DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                        self.getArrivalsAct()
                    }
                }
        }
    }
    
    func stopArrivalsLoop() {
        self.busStopCode = nil
    }
    
    private func getTime() -> String {
        let time = Date()
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}

enum BusStopArrivalsScreenState {
    case Fetching
    case Success(busStopArrivalItemDataList: [BusStopArrivalItemData], lastUpdatedOn: String)
}

class BusStopArrivalItemData : ObservableObject, Identifiable {
    var id: UUID?
    @Published var busStopArrival: BusStopArrival
    @Published var lastUpdatedOn: String
    
    init(busStopArrival: BusStopArrival) {
        id = UUID()
        self.busStopArrival = busStopArrival
        self.lastUpdatedOn = "NONO"
    }
}
