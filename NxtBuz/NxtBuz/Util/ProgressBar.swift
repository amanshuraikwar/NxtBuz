//
//  ProgressBar.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 19/10/21.
//

import SwiftUI

struct ProgressBar: View {
    var progress: Float
    
    var body: some View {
        ZStack {
            Circle()
                .stroke(lineWidth: 4.0)
                .opacity(0.3)
                .foregroundColor(Color.accentColor)
            
            Circle()
                .trim(from: 0.0, to: CGFloat(min(self.progress, 1.0)))
                .stroke(style: StrokeStyle(lineWidth: 4.0, lineCap: .round, lineJoin: .round))
                .foregroundColor(Color.accentColor)
                .rotationEffect(Angle(degrees: 270.0))
                .animation(.linear)

//            Text(String(format: "%.0f %%", min(self.progress, 1.0)*100.0))
//                .font(.largeTitle)
//                .bold()
        }
    }
}

//struct ProgressBar_Previews: PreviewProvider {
//    static var previews: some View {
//        ProgressBar()
//    }
//}
