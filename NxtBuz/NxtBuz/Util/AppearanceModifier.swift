//
//  AppearanceModifier.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 30/09/21.
//

import Foundation
import SwiftUI

struct AppearanceModifier: ViewModifier {
    let textColor: UIColor
    
    init(textColor: UIColor) {
        self.textColor = textColor
        let navBarAppearance = UINavigationBarAppearance()
        navBarAppearance.titleTextAttributes = [.foregroundColor: textColor]
        navBarAppearance.largeTitleTextAttributes = [.foregroundColor: textColor]
        UINavigationBar.appearance().standardAppearance = navBarAppearance
        UINavigationBar.appearance().compactAppearance = navBarAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navBarAppearance
        UINavigationBar.appearance().tintColor = textColor
    }

    func body(content: Content) -> some View {
        content
    }
}

extension View {
    func appearanceModifier(textColor: UIColor) -> some View {
        self.modifier(AppearanceModifier(textColor: textColor))
    }
}
