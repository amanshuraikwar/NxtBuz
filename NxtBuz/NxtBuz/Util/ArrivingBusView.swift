//
//  StarredArrivingBusView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 27/09/21.
//

import SwiftUI
import iosUmbrella

struct ArrivingBusView: View {
    let arrivingBus: ArrivingBus
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        HStack {
            Image(getBusTypeImageName(arrivingBus.type))
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 16, height: 16)
            
            Spacer()
            
            Text(getBusArrivalStr(Int(arrivingBus.arrival)))
                .font(NxtBuzFonts.bodyMonospaced)
                .fontWeight(.bold)
                
            Spacer()
            
            Image(getBusLoadImageName(arrivingBus.load))
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 16, height: 16)
            
            Image(getWheelCharAccessImageName(arrivingBus.wheelchairAccess))
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 16, height: 16)
        }
        .padding(.vertical, 2)
        .padding(.horizontal, 4)
        .background(Color(.systemGray5).opacity(0.4))
        .cornerRadius(8)
        .padding(.bottom, 4)
    }
    
    private func getBusTypeImageName(_ busType: BusType) -> String {
        if (busType == BusType.dd) {
            return "BusTypeDd"
        } else if (busType == BusType.bd) {
            return "BusTypeFeeder"
        } else {
            return "BusTypeNormal"
        }
    }
    
    private func getBusLoadImageName(_ busLoad: BusLoad) -> String {
        if busLoad == BusLoad.sea {
            return "BusLoad1"
        } else if busLoad == BusLoad.sda {
            return "BusLoad2"
        } else if busLoad == BusLoad.lsd {
            return "BusLoad3"
        } else {
            return "BusLoad0"
        }
    }
    
    private func getBusArrivalStr(_ arrival: Int) -> String {
        if (arrival >= 60) {
            return "60+ Mins"
        } else if (arrival > 1) {
            return String(format: "%02d Mins", arrival)
        } else if (arrival == 1) {
            return String(format: "%02d Min", arrival)
        } else {
            return "NOW"
        }
    }
    
    private func getWheelCharAccessImageName(_ wheelchairAccess: Bool) -> String {
        if wheelchairAccess {
            return "Accessible"
        } else {
            return "NotAccessible"
        }
    }
}

//struct StarredArrivingBusView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredArrivingBusView()
//    }
//}
