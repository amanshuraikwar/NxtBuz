//
//  NxtBuzTheme.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 29/09/21.
//

import Foundation
import UIKit
import iosUmbrella

class NxtBuzTheme : ObservableObject {
    @Published var isDark = true
    @Published var primaryColor = NxtBuzTheme.defaultTheme.darkThemeColors.primary
    @Published var secondaryColor = NxtBuzTheme.defaultTheme.darkThemeColors.primary
    @Published var accentColor = NxtBuzTheme.defaultTheme.darkThemeColors.primary
    
    private var theme = defaultTheme
    
    func updateTheme(isDark: Bool) {
        print("Theme: \(isDark)")
        self.isDark = isDark
        self.primaryColor = isDark ? theme.darkThemeColors.primary : theme.lightThemeColors.primary
        self.secondaryColor = isDark ? theme.darkThemeColors.secondary : theme.lightThemeColors.secondary
        self.accentColor = isDark ? theme.darkThemeColors.accent : theme.lightThemeColors.accent
    }
    
    func fetchTheme() {
        updateTheme(Di.get().getThemeUseCase().getThemeSync())
        Di.get().getThemeUseCase().getThemeUpdates { theme in
            DispatchQueue.main.sync {
                self.updateTheme(theme)
            }
        }
    }
    
    private func updateTheme(_ theme: DynamoTheme) {
        self.theme = theme
        self.primaryColor = isDark ? theme.darkThemeColors.primary : theme.lightThemeColors.primary
        self.secondaryColor = isDark ? theme.darkThemeColors.secondary : theme.lightThemeColors.secondary
        self.accentColor = isDark ? theme.darkThemeColors.accent : theme.lightThemeColors.accent
    }
    
    public static let defaultTheme = DynamoTheme(
        darkThemeColors: DynamoThemeColors(
            primary: UIColor(.white),
            secondary: UIColor(.gray),
            accent: UIColor(.blue)
        ),
        lightThemeColors: DynamoThemeColors(
            primary: UIColor(.black),
            secondary: UIColor(.gray),
            accent: UIColor(.green)
        )
    )
}
