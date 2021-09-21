//
//  BusStopsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import Foundation
import iosUmbrella

class BusStopsViewModel : ObservableObject {
    @Published var busStopsScreenState: BusStopsScreenState = .Fetching
    
    private let getBusStopsUseCase = Di.get().getBusStopsUseCase()
    
    func fetchBusStops() {
        switch busStopsScreenState {
            case BusStopsScreenState.Fetching:
                getBusStopsUseCase.invoke(
                    lat: 1.3416,
                    lon: 103.7757,
                    limit: 50
                ) { busStopList in
                    DispatchQueue.main.async {
                        self.busStopsScreenState =
                            .Success(busStopList: busStopList)
                    }
                }
            default:
                break
        }
    }
}

enum BusStopsScreenState {
    case Fetching
    case Success(busStopList: [BusStop])
}
