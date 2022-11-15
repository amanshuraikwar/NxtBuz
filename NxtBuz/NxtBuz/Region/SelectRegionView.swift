//
//  SelectRegionView.swift
//  NxtBuz
//
//  Created by Amanshu Raikwar on 15/11/22.
//

import SwiftUI

struct SelectRegionView: View {
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    enum Region: String, CaseIterable, Identifiable {
        case singapore, netherlands
        var id: Self { self }
    }
    
    @State private var selectedRegion: Region = .singapore
    
    var body: some View {
        VStack {
            Spacer()
            
            Text("Select region:")
                .font(NxtBuzFonts.headline)
                .frame(maxWidth: .infinity, alignment: .center)
            
            Spacer()
            
            Picker("", selection: $selectedRegion) {
                Text("\(flag(from: "SG")) Singapore")
                    .tag(Region.singapore)
                    .font(NxtBuzFonts.title)
                Text("\(flag(from: "NL")) The Netherlands")
                    .tag(Region.netherlands)
                    .font(NxtBuzFonts.title)
            }
            .pickerStyle(WheelPickerStyle())
            
            Spacer()
            
            PrimaryButton(
                text: "Select",
                action: {  },
                iconSystemName: "chevron.forward"
            )
            .padding()
        }
    }
    
    func flag(from country:String) -> String {
        let base : UInt32 = 127397
        var s = ""
        for v in country.uppercased().unicodeScalars {
            s.unicodeScalars.append(UnicodeScalar(base + v.value)!)
        }
        return s
    }
}

//struct SelectRegionView_Previews: PreviewProvider {
//    static var previews: some View {
//        SelectRegionView()
//    }
//}
