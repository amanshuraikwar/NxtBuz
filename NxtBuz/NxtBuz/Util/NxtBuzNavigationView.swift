//
//  SearchNavigationView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 27/09/21.
//

import Foundation
import SwiftUI
import UIKit

extension UIFont {
    class func rounded(ofSize size: CGFloat, weight: UIFont.Weight) -> UIFont {
        let systemFont = UIFont.systemFont(ofSize: size, weight: weight)

        guard #available(iOS 13.0, *), let descriptor = systemFont.fontDescriptor.withDesign(.rounded) else { return systemFont }
        return UIFont(descriptor: descriptor, size: size)
    }
}

// ref: https://kavsoft.dev/SwiftUI_2.0/Navigation_SearchBar
struct NxtBuzNavigationView : UIViewControllerRepresentable {
    var view: AnyView
    let title: String
    let searchPlaceholder: String
    var onSearch: (String) -> Void
    var onCancel: () -> Void
    
    private var searchFeature = false
    
    init(
        _ view: AnyView,
        title: String,
        searchPlaceholder: String,
        onSearch: @escaping (String) -> Void,
        onCancel: @escaping () -> Void
    ) {
        self.view = view
        self.title = title
        self.searchPlaceholder = searchPlaceholder
        self.onSearch = onSearch
        self.onCancel = onCancel
        self.searchFeature = true
    }
    
    init(
        _ view: AnyView,
        title: String
    ) {
        self.view = view
        self.title = title
        self.searchPlaceholder = ""
        self.onSearch = {_ in}
        self.onCancel = {}
        self.searchFeature = false
    }
    
    func makeCoordinator() -> Coordinator {
        return NxtBuzNavigationView.Coordinator(parent: self)
    }
    
    func makeUIViewController(context: Context) -> some UIViewController {
        let childView = UIHostingController(rootView: view)
        
        let controller = UINavigationController(rootViewController: childView)
        controller.navigationBar.topItem?.title = title
        controller.navigationBar.prefersLargeTitles = true
        controller.navigationBar.titleTextAttributes = [NSAttributedString.Key.font: UIFont.rounded(ofSize: 20, weight: .bold)]
        controller.navigationBar.largeTitleTextAttributes = [NSAttributedString.Key.font: UIFont.rounded(ofSize: 34, weight: .bold)]

        if searchFeature {
            let searchController = UISearchController()
            searchController.searchBar.placeholder = searchPlaceholder
            searchController.searchBar.delegate = context.coordinator
            searchController.obscuresBackgroundDuringPresentation = false
            
            controller.navigationBar.topItem?.hidesSearchBarWhenScrolling = false
            controller.navigationBar.topItem?.searchController = searchController
        }

        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        
    }
    
    class Coordinator : NSObject, UISearchBarDelegate {
        var parent: NxtBuzNavigationView
        
        init(parent: NxtBuzNavigationView) {
            self.parent = parent
        }
        
        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            self.parent.onSearch(searchText)
        }
        
        func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
            self.parent.onCancel()
        }
    }
}

