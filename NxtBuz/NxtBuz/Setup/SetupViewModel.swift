//
//  SetupViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import Foundation
import iosUmbrella
import WidgetKit

class SetupViewModel : ObservableObject {
    @Published var setupScreenState: SetupScreenState = .NotStarted
    
    func startSetup() {
        switch setupScreenState {
        case .NotStarted, .Failed:
            Di.get().getDoSetupUserStateUserCase().invoke { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Success(let setupState):
                    if let inProgress  = setupState as? SetupState.InProgress {
                        Util.onMain {
                            self.setupScreenState =
                                .InProgress(progress: Float(inProgress.progress))
                        }
                    }
                    
                    if let _  = setupState as? SetupState.Complete {
                        Util.onMain {
                            self.setupScreenState = .Complete
                            WidgetCenter.shared.reloadTimelines(ofKind: "io.github.amanshuraikwar.NxtBuz.busArrivalWidget")
                        }
                    }
                case .Error(let message):
                    print(message)
                }
            }
        default:
            break
        }
    }
}

enum SetupScreenState {
    case NotStarted
    case InProgress(progress: Float)
    case Complete
    case Failed
}
