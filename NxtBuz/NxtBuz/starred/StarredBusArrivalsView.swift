//
//  StarredBusArrivalsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 23/09/21.
//

import SwiftUI

struct StarredBusArrivalsView: View {
    var body: some View {
        if #available(iOS 15.0, *) {
            VStack(
                alignment: .leading,
                spacing: 0
            ) {
                ScrollView(
                    .horizontal,
                    showsIndicators: false
                ) {
                    HStack(
                        spacing: 0
                    ) {
                        ForEach(0...50, id: \.self) { index in
                            if index == 50 {
                                StarredBusArrivalsItemView(error: index % 2 == 0)
                                    .padding()
                                    .shadow(color: Color.black.opacity(0.1), radius: 4)
                            } else {
                                StarredBusArrivalsItemView(error: index % 2 == 0)
                                    .padding(.vertical)
                                    .padding(.leading)
                                    .shadow(color: Color.black.opacity(0.1), radius: 4)
                            }
                        }
                    }
                }
            }
            .background(.ultraThinMaterial)
            .shadow(color: Color(.systemGray5).opacity(0.4), radius: 4)
            .frame(
                width: UIScreen.main.bounds.width
            )
        } else {
            ScrollView(.horizontal) {
                LazyHStack {
                    ForEach(0...50, id: \.self) { index in
                        StarredBusArrivalsItemView(error: index % 2 == 0)
                    }
                }
            }
            .padding()
            .frame(
                maxWidth: .infinity
            )
            .background(Color(.systemGray))
        }
    }
}

struct StarredBusArrivalsView_Previews: PreviewProvider {
    static var previews: some View {
        StarredBusArrivalsView()
    }
}
