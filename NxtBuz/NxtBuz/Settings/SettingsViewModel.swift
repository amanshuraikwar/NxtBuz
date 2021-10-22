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
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(_):
                    Util.onMain {
                        self.homeStopState = .NoBusStop
                    }
                case .Success(let data):
                    let busStop = data
                    Util.onMain {
                        self.homeStopState = .Success(
                            desc: busStop.description_,
                            roadName: busStop.roadName,
                            busStopCode: busStop.code
                        )
                    }
                }
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
