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
        ZStack {
            if nxtBuzTheme.themeInit {
                SelectRegionView()//HomeView()
                    .accentColor(Color(nxtBuzTheme.accentColor))
            } else {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
            }
        }
        .environmentObject(nxtBuzTheme)
        .onAppear {
            nxtBuzTheme.initTheme(isSystemInDarkMode: colorScheme == .dark)
        }
        .onChange(of: colorScheme) { colorScheme in
            nxtBuzTheme.onSystemThemeChanged(isDark: colorScheme == .dark)
        }
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView()
//    }
//}
