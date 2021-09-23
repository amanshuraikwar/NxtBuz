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
                Text("Starred Buses")
                    .font(NxtBuzFonts.body)
                    .fontWeight(.bold)
                    .foregroundColor(.primary)
                    .padding(.top)
                    .padding(.horizontal)
                
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
                                    .padding(.top)
                                    .padding(.bottom, 2)
                                    .padding(.horizontal)
                                    .shadow(color: Color(.systemGray4), radius: 2)
                            } else {
                                StarredBusArrivalsItemView(error: index % 2 == 0)
                                    .padding(.top)
                                    .padding(.bottom, 2)
                                    .padding(.leading)
                                    .shadow(color: Color(.systemGray4), radius: 2)
                            }
                        }
                    }
                }
            }
            .background(.ultraThinMaterial)
//            .cornerRadius(20)
//            .padding(.horizontal)
            .shadow(color: Color(.systemGray5), radius: 4)
            .frame(
                width: UIScreen.main.bounds.width
            )
            //.padding()
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
