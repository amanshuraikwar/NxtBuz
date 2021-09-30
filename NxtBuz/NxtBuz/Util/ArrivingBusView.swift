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
        HStack(
            spacing: 16
        ) {
            Image(getBusTypeImageName(arrivingBus.type))
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundColor(Color(nxtBuzTheme.primaryColor))
                .padding(4)
                .background(Color(.systemGray5))
                .cornerRadius(8)
            
            ZStack {   
                Text(getBusArrivalStr(Int(arrivingBus.arrival)))
                        .font(NxtBuzFonts.title2Monospaced)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
                        .fontWeight(.bold)
                
                Text("NOW ")
                        .font(NxtBuzFonts.title2Monospaced)
                        .fontWeight(.bold)
                        .opacity(0.0)
            }

            Image(getBusLoadImageName(arrivingBus.load))
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            
            Image(getWheelCharAccessImageName(arrivingBus.wheelchairAccess))
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
        }
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
            return "60+"
        } else if (arrival > 0) {
            return String(format: "%02d", arrival)
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
