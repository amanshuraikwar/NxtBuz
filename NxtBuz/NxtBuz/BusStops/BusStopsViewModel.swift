//
//  BusStopsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import Foundation
import iosUmbrella
import CoreLocation

class BusStopsViewModel : NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var busStopsScreenState: BusStopsScreenState = .Fetching(message: "Fetching bus stops...")
    private let getBusStopsUseCase = Di.get().getBusStopsUseCase()
    
    private let locationManager: CLLocationManager

    override init() {
        locationManager = CLLocationManager()
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
    }
    
    func fetchBusStops(showFetching: Bool = false) {
        if showFetching {
            self.busStopsScreenState = .Fetching(message: "Fetching bus stops...")
        }
        
        switch busStopsScreenState {
        case .Fetching, .AskLocationPermission, .Error, .GoToSettingsLocationPermission:
            fetchBusStopsAct()
        case .Success(_, _, let searchResults, let locationLowAccuracy):
            if !searchResults && locationLowAccuracy {
                fetchBusStopsAct()
            }
        }
    }
    
    private func fetchBusStopsAct() {
        var lat = -1.0
        var lng = -1.0
        var lowAccuracy = false
        
        switch locationManager.authorizationStatus {
        case .notDetermined:
            busStopsScreenState = .AskLocationPermission
        case .restricted:
            busStopsScreenState = .Error(
                errorMessage: "Location use is restricted, please try again."
            )
        case .denied:
            busStopsScreenState = .GoToSettingsLocationPermission
        case .authorizedAlways, .authorizedWhenInUse:
            lowAccuracy = locationManager.accuracyAuthorization == .reducedAccuracy
            lat = locationManager.location?.coordinate.latitude ?? -1.0
            lng = locationManager.location?.coordinate.longitude ?? -1.0
        default:
            busStopsScreenState = .Error(
                errorMessage: "Something went wrong, please try again."
            )
        }
    
        if lat == -1.0 || lng == -1.0 {
            return
        }
        
        getBusStopsUseCase.invoke(
            lat: lat,
            lon: lng,
            limit: 50
        ) { busStopList in
            DispatchQueue.main.sync {
                self.busStopsScreenState =
                    .Success(
                        header: "Nearby Bus Stops",
                        busStopList: busStopList,
                        searchResults: false,
                        locationLowAccuracy: lowAccuracy
                    )
            }
        }
    }
    
    func requestPermission() {
        locationManager.requestWhenInUseAuthorization()
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        self.busStopsScreenState = .Fetching(message: "Fetching nearby bus stops...")
        self.fetchBusStops()
    }
    
    func searchBusStops(_ searchString: String) {
        Di.get()
            .getSearchUseCase()
            .invoke(
                query: searchString,
                limit: 50
            ) { searchResult in
                DispatchQueue.main.sync {
                    self.busStopsScreenState =
                        .Success(
                            header: "Matching Bus Stops",
                            busStopList: searchResult.busStopList,
                            searchResults: true,
                            locationLowAccuracy: false
                        )
                }
            }
    }
    
    func onSearch(searchString: String) {
        if searchString == "" {
            self.busStopsScreenState = .Fetching(message: "Fetching nearby bus stops...")
            fetchBusStops()
        } else {
            self.busStopsScreenState = .Fetching(message: "Searching bus stops...")
            searchBusStops(searchString)
        }
    }
}

enum BusStopsScreenState {
    case Fetching(message: String)
    case AskLocationPermission
    case Error(errorMessage: String)
    case GoToSettingsLocationPermission
    case Success(header: String, busStopList: [BusStop], searchResults: Bool, locationLowAccuracy: Bool)
}
