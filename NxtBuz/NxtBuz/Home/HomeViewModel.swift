//
//  HomeViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import Foundation
import iosUmbrella
import WidgetKit
import SwiftUI

class HomeViewModel : ObservableObject {
    @Published var screenState: HomeScreenState = HomeScreenState.Fetching
    @Published var tabSelection = 1
        
    @Published var busStopArrivalsSheetData: BusStopArrivalsSheetData? = nil
    
    @Published var showingAlert: Bool = false
    
    func getUserState() {
        Di.get().getUserStateUserCase().invoke { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(let message):
                print(message)
            case .Success(let userState):
                if (userState is UserState.New) {
                    DispatchQueue.main.async {
                        self.screenState = HomeScreenState.Setup
                    }
                }
                
                if (userState is UserState.SetupComplete) {
                    DispatchQueue.main.async {
                        self.screenState = HomeScreenState.BusStops
                    }
                }
            }
        }
        WidgetCenter.shared.reloadTimelines(ofKind: "io.github.amanshuraikwar.NxtBuz.busArrivalWidget")
    }
    
    func onDeeplinkUrlOpen(url: URL) {
        if let scheme = url.scheme {
            if scheme == "starredBusArrivalsWidget" {
                WidgetCenter.shared.reloadTimelines(
                    ofKind: "io.github.amanshuraikwar.NxtBuz.starredBusArrivalsWidget"
                )
                self.tabSelection = 2
            }
            
            if scheme == "busArrivalWidget" {
                WidgetCenter.shared.reloadTimelines(
                    ofKind: "io.github.amanshuraikwar.NxtBuz.busArrivalWidget"
                )
            }
            
            if scheme == "goingHomeBusWidget" {
                WidgetCenter.shared.reloadTimelines(
                    ofKind: "io.github.amanshuraikwar.NxtBuz.goingHomeBusWidget"
                )
            }
            
            if scheme == "nextTrainWidget" {
                WidgetCenter.shared.reloadTimelines(
                    ofKind: "io.github.amanshuraikwar.NxtBuz.NextTrainWidget"
                )
            }
        }
    }
}

extension URL {
    func valueOf(_ queryParamaterName: String) -> String? {
        guard let url = URLComponents(string: self.absoluteString) else { return nil }
        return url.queryItems?.first(where: { $0.name == queryParamaterName })?.value
    }
}

enum HomeScreenState {
    case Fetching
    case Setup
    case BusStops
}

struct BusStopArrivalsSheetData : Identifiable {
    let id = UUID()
    let busStopCode: String
}
