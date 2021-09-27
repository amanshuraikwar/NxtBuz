//
//  SearchNavigationView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 27/09/21.
//

import Foundation
import SwiftUI

// ref: https://kavsoft.dev/SwiftUI_2.0/Navigation_SearchBar

struct SearchNavigationView : UIViewControllerRepresentable {
    var view: AnyView
    let title: String
    let searchPlaceholder: String
    var onSearch: (String) -> Void
    var onCancel: () -> Void
    
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
    }
    
    func makeCoordinator() -> Coordinator {
        return SearchNavigationView.Coordinator(parent: self)
    }
    
    func makeUIViewController(context: Context) -> some UIViewController {
        let childView = UIHostingController(rootView: view)
        
        let controller = UINavigationController(rootViewController: childView)
        controller.navigationBar.topItem?.title = title
        controller.navigationBar.prefersLargeTitles = true
        
        let searchController = UISearchController()
        searchController.searchBar.placeholder = searchPlaceholder
        searchController.searchBar.delegate = context.coordinator
        searchController.obscuresBackgroundDuringPresentation = false

        controller.navigationBar.topItem?.hidesSearchBarWhenScrolling = false
        controller.navigationBar.topItem?.searchController = searchController
        
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        
    }
    
    class Coordinator : NSObject, UISearchBarDelegate {
        var parent: SearchNavigationView
        
        init(parent: SearchNavigationView) {
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
