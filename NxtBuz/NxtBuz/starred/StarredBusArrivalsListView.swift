//
//  StarredBusArrivalsListView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import SwiftUI

@available(iOS 15.0, *)
struct StarredBusArrivalsListView: View {
    @StateObject var data: StarredBusArrivalsScreenSuccessData
    
    var body: some View {
        ZStack {
            if data.shouldShowList {
                ScrollView(
                    .horizontal,
                    showsIndicators: false
                ) {
                    HStack(
                        spacing: 0
                    ) {
    //                    var index = -1
                        ForEach(data.starredBusArrivalItemDataList) { starredBusArrivalItemData in
    //                        if index == data.starredBusArrivalItemDataList.count - 1 {
    //                            StarredBusArrivalsItemView(
    //                                starredBusArrivalItemData: data.starredBusArrivalItemDataList[index]
    //                            )
    //                                .padding()
    //                                .shadow(color: Color.black.opacity(0.1), radius: 4)
    //                        } else {
                                StarredBusArrivalsItemView(
                                    starredBusArrivalItemData: starredBusArrivalItemData
                                )
                                    .padding(.vertical)
                                    .padding(.leading)
                                    .shadow(color: Color.black.opacity(0.1), radius: 4)
    //                        }
                        }
                        
                        Text("")
                            .padding(.trailing)
                    }
                }
                .background(.ultraThinMaterial)
                .shadow(color: Color(.systemGray5).opacity(0.4), radius: 4)
                .frame(
                    width: UIScreen.main.bounds.width
                )
            } else {
                Text("")
            }
        }
    }
}

//struct StarredBusArrivalsListView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsListView()
//    }
//}
