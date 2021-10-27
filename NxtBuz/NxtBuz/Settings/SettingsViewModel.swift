//
//  SettingsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 16/10/21.
//

import Foundation
import iosUmbrella
import SwiftUI

class SettingsViewModel : ObservableObject {
    @Published var homeStopState: HomeBusStopState = .Fetching
    @Published var cachedDirectBusDataState: CachedDirectBusDataState = .Fetching
    
    func fetchHomeBusStop() {
        Di.get()
            .getHomeBusStopUseCase()
            .invoke { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(_):
                    Util.onMain {
                        self.homeStopState = .NoBusStop
                    }
                case .Success(let data):
                    if let success = data as? HomeBusStopResult.Success {
                        let busStop = success.busStop
                        Util.onMain {
                            self.homeStopState = .Success(
                                desc: busStop.description_,
                                roadName: busStop.roadName,
                                busStopCode: busStop.code
                            )
                        }
                    }
                    
                    if data is HomeBusStopResult.NotSet {
                        Util.onMain {
                            self.homeStopState = .NoBusStop
                        }
                    }
                }
            }
        
        Di.get()
            .getCachedDirectBusDataUseCase()
            .invoke { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(_):
                    Util.onMain {
                        self.cachedDirectBusDataState = .Success(count: 0)
                    }
                case .Success(let data):
                    Util.onMain {
                        self.cachedDirectBusDataState = .Success(count: Int(truncating: data))
                    }
                }
            }
    }
}

enum CachedDirectBusDataState {
    case Fetching
    case Success(
        count: Int
    )
}

enum HomeBusStopState {
    case Fetching
    case Success(
        desc: String,
        roadName: String,
        busStopCode: String
    )
    case NoBusStop
}
