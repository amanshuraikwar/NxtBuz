//
//  HomeViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import Foundation
import iosUmbrella

class HomeViewModel : ObservableObject {
    @Published var screenState: HomeScreenState = HomeScreenState.Fetching
    
    private let getUserStateUseCase = Di.get().getUserStateUserCase()
    
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
    }
}

enum HomeScreenState {
    case Fetching
    case Setup
    case BusStops
}
