//
//  BusStopsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import Foundation
import iosUmbrella
import CoreLocation
import WidgetKit

class BusStopsViewModel : NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var busStopsScreenState: BusStopsScreenState = .Fetching(message: "Fetching bus stops...")
    private let getBusStopsUseCase = Di.get().getBusStopsUseCase()
    
    private let locationManager: CLLocationManager
    
    @Published var busesGoingHomeState: BusesGoingHomeState = .Fetching
    
    private var cancellationSignal: FlowCancellationSignal? = nil

    override init() {
        locationManager = CLLocationManager()
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
    }
    
    func fetchBusStops(showFetching: Bool = false) {
//        getTrainsBetween()
        if showFetching {
            self.busStopsScreenState = .Fetching(message: "Fetching bus stops...")
            self.busesGoingHomeState = .Fetching
            self.cancellationSignal?.cancel()
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
        ) { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(let message):
                print(message)
            case .Success(let data):
                let busStopList = data.compactMap({ $0 as? BusStop })
                Util.onMain {
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
        
        Di.get()
            .getNearbyGoingHomeBusesUseCase()
            .invoke(
                lat: lat,
                lng: lng,
                onStart: { cancellationSignal in
                    Util.onMain {
                        self.cancellationSignal?.cancel()
                        self.cancellationSignal = cancellationSignal
                    }
                }
            ) { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(let message):
                    print(message)
                case .Success(let data):
                    Util.onMain {
                        if let data = data as? GoingHomeBusResult.Success {
                            if data.directBuses.count > 2 {
                                self.busesGoingHomeState = .Success(result: data, showMore: true, showLess: false)
                            } else {
                                self.busesGoingHomeState = .Success(result: data, showMore: false, showLess: false)
                            }
                        } else {
                            self.busesGoingHomeState = .Success(result: data, showMore: false, showLess: false)
                        }
                    }
                }
            }
    }
    
    func onShowMoreGoingHomeBusesClick() {
        switch self.busesGoingHomeState {
        case .Success(let result, _, _):
            self.busesGoingHomeState = .Success(result: result, showMore: false, showLess: true)
        case .Fetching:
            {}()
        }
    }
    
    func onShowLessGoingHomeBusesClick() {
        switch self.busesGoingHomeState {
        case .Success(let result, _, _):
            self.busesGoingHomeState = .Success(result: result, showMore: true, showLess: false)
        case .Fetching:
            {}()
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
            ) { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(let message):
                    print(message)
                    Util.onMain {
                        self.busStopsScreenState = .Error(errorMessage: message)
                    }
                case .Success(let searchResult):
                    Util.onMain {
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
    }
    
    func onSearch(searchString: String) {
        if searchString == "" {
            fetchBusStops(showFetching: true)
        } else {
            self.busStopsScreenState = .Fetching(message: "Searching bus stops...")
            self.busesGoingHomeState = .Fetching
            self.cancellationSignal?.cancel()
            searchBusStops(searchString)
        }
    }
    
    func onSetHomeClick(busStopCode: String) {
        Di.get()
            .setHomeBusStopUseCase()
            .invoke(
                busStopCode: busStopCode,
                completion: { result in
                    let useCaseResult = Util.toUseCaseResult(result)
                    switch useCaseResult {
                    case .Error(let message):
                        print(message)
                    case .Success(_):
                        WidgetCenter.shared.reloadTimelines(
                            ofKind: "io.github.amanshuraikwar.NxtBuz.goingHomeBusWidget"
                        )
                        print("set home bus stop \(busStopCode) success")
                    }
                }
            )
    }
    
    private func getTrainsBetween() {
        Di.get().getTrainBetweenStopsUseCase().invoke1(
            fromTrainStopCode: "NS-API-TRAIN-UT",
            toTrainStopCode: "NS-API-TRAIN-ASD"
        ) { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(let message):
                NSLog("yoyoyo, \(message)")
            case .Success(let output):
                NSLog("yoyoyo, \(output)")
            }
        }
    }
}

enum BusesGoingHomeState {
    case Fetching
    case Success(
        result: GoingHomeBusResult,
        showMore: Bool,
        showLess: Bool
    )
}

enum BusStopsScreenState {
    case Fetching(message: String)
    case AskLocationPermission
    case Error(errorMessage: String)
    case GoToSettingsLocationPermission
    case Success(
        header: String,
        busStopList: [BusStop],
        searchResults: Bool,
        locationLowAccuracy: Bool
    )
}
