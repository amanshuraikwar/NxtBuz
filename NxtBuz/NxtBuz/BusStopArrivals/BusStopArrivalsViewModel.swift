//
//  BusStopArrivalsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import Foundation
import iosUmbrella

class BusStopArrivalsViewModel : ObservableObject {
    @Published var screenState: BusStopArrivalsScreenState = .Fetching
    
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
                    DispatchQueue.main.async {
                        self.screenState = .Success(busStopArrivalList: busStopArrivalList, lastUpdatedOn: self.getTime())
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
    case Success(busStopArrivalList: [BusStopArrival], lastUpdatedOn: String)
}
