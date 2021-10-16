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
    
    func fetchHomeBusStop() {
        Di.get()
            .getHomeBusStopUseCase()
            .invoke { result in
                if let result = result as? IosResultSuccess {
                    if let busStop = result.data {
                        DispatchQueue.main.sync {
                            self.homeStopState = .Success(
                                desc: busStop.description_,
                                roadName: busStop.roadName,
                                busStopCode: busStop.code
                            )
                        }
                    } else {
                        DispatchQueue.main.sync {
                            self.homeStopState = .NoBusStop
                        }
                    }
                }
                
//                if let result = result as? IosResultError {
//                    DispatchQueue.main.sync {
//                        homeStopState = .NoBusStop
//                    }
//                }
            }
    }
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
