//
//  NextTrainWidgets.swift
//  NextTrainWidgetExtension
//
//  Created by Amanshu Raikwar on 8/11/22.
//

import SwiftUI
import WidgetKit

@main
struct NextTrainWidgets: WidgetBundle {
    var body: some Widget {
        NextTrainWidget()

        if #available(iOS 16.1, *) {
            NextTrainLiveActivity()
        }
    }
}
