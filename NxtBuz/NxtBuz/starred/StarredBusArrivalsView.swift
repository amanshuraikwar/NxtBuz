//
//  StarredBusArrivalsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 23/09/21.
//

import SwiftUI
import iosUmbrella

struct StarredBusArrivalsView: View {
    @StateObject private var viewModel = StarredBusArrivalsViewModel()
    
    var body: some View {
        if #available(iOS 15.0, *) {
            ZStack {
                switch viewModel.screenState {
                case .Fetching, .Error:
                    Text("")
                case .Success(let data):
                    if !data.starredBusArrivalItemDataList.isEmpty {
                        StarredBusArrivalsListView(data: data)
                    }
                }
            }
            .onAppear {
                viewModel.getArrivals()
            }
        } else {
            Text("#todo")
        }
    }
}

struct StarredBusArrivalsView_Previews: PreviewProvider {
    static var previews: some View {
        StarredBusArrivalsView()
    }
}
