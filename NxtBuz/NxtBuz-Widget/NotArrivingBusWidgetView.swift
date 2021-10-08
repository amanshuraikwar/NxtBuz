//
//  NotArrivingBusWidgetView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 08/10/21.
//

import SwiftUI

struct NotArrivingBusWidgetView: View {
    let busStopCode: String
    let busStopDescription: String
    let busServiceNumber: String
    let errorMessage: String
    let updated: Date
    
    @EnvironmentObject private var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        GeometryReader { geometry in
            HStack(
                spacing: 0
            ) {
                VStack(
                    spacing: 0
                ) {
                    HStack {
                        ZStack {
                            Text(busServiceNumber)
                                .font(NxtBuzFonts.title3)
                                .fontWeight(.medium)
                                .foregroundColor(Color(.white))
                            
                            Text("961M")
                                .font(NxtBuzFonts.title3)
                                .fontWeight(.medium)
                                .opacity(0.0)
                        }
                        .padding(.vertical, 2)
                        .padding(.horizontal, 4)
                        .background(Color(nxtBuzTheme.accentColor))
                        .clipShape(Capsule())
                        
                        Spacer()
                    }

                    Text(busStopDescription)
                        .font(NxtBuzFonts.footnote)
                        .fontWeight(.medium)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.top, 8)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)

                    
                    Spacer()
                    
                    Text("\(getTime(date: updated))")
                        .font(NxtBuzFonts.caption)
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(.vertical)
                .padding(.leading)
                .padding(.trailing, 8)
                .frame(width: geometry.size.width / 2.5)
                
                VStack(
                    spacing: 0
                ) {
                    Text(errorMessage)
                        .font(NxtBuzFonts.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .multilineTextAlignment(.leading)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .fixedSize(horizontal: false, vertical: true)
                    
                    Spacer()
                    
                    HStack {
                        Spacer()
                        
                        Link(destination: URL(string: "busArrivalWidget://refreshBusStopArrivals?busStopCode=\(busStopCode)&busServiceNumber=\(busServiceNumber)")!) {
                            Image(systemName: "arrow.triangle.2.circlepath")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 18, height: 18)
                                .padding(6)
                                .foregroundColor(Color(nxtBuzTheme.accentColor))
                                .background(.white)
                                .cornerRadius(12)
                        }
                    }
                }
                .foregroundColor(.white)
                .padding(.vertical)
                .padding(.trailing)
                .padding(.leading)
                .frame(width: geometry.size.width * 3 / 5)
                .background(Color(nxtBuzTheme.accentColor))
            }
        }
        .frame(maxWidth: .infinity)
    }
    
    public func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}
//
//struct NotArrivingBusWidgetView_Previews: PreviewProvider {
//    static var previews: some View {
//        NotArrivingBusWidgetView()
//    }
//}
