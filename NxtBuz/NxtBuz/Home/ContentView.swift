//
//  ContentView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 15/09/21.
//

import SwiftUI


struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var nxtBuzTheme = NxtBuzTheme()
    
    var body: some View {
        HomeView()
            .environmentObject(nxtBuzTheme)
            .onAppear {
                nxtBuzTheme.updateTheme(isDark: colorScheme == .dark)
                nxtBuzTheme.fetchTheme()
            }
            .onChange(of: colorScheme) { colorScheme in
                nxtBuzTheme.updateTheme(isDark: colorScheme == .dark)
            }
            .accentColor(Color(nxtBuzTheme.accentColor))
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView()
//    }
//}
