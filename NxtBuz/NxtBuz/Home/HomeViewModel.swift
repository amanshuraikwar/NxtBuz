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
    
    private let getUserStateUseCase = Di.get().getUserStateUserCase()
    
    @Published var busStopArrivalsSheetData: BusStopArrivalsSheetData? = nil
    
    @Published var showingAlert: Bool = false
    
    func getUserState() {
        getUserStateUseCase.invoke { userState in
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
        WidgetCenter.shared.reloadTimelines(ofKind: "io.github.amanshuraikwar.NxtBuz.busArrivalWidget")
    }
    
    func onDeeplinkUrlOpen(url: URL) {
        WidgetCenter.shared.reloadTimelines(ofKind: "io.github.amanshuraikwar.NxtBuz.busArrivalWidget")
        if let host = url.host {
            if host == "open" {
                if let busStopDescription = url.valueOf("desc") {
                    
                }
            } else if host == "refreshBusStopArrivals" {
                showingAlert = true
            }
        }
        
        if let busStopCode = url.valueOf("busStopCode") {
            //self.tabSelection = 1
            //self.busStopArrivalsSheetData = BusStopArrivalsSheetData(busStopCode: busStopCode)
        }
    }
}

extension URL {
    func valueOf(_ queryParamaterName: String) -> String? {
        guard let url = URLComponents(string: self.absoluteString) else { return nil }
        print("ypoyoyoyo" + url.path)
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
