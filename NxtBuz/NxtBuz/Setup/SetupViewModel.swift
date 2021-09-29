//
//  SetupViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import Foundation
import iosUmbrella

class SetupViewModel : ObservableObject {
    @Published var setupScreenState: SetupScreenState = .NotStarted
    
    private let doSetupUseCase = Di.get().getDoSetupUserStateUserCase()
    
    func startSetup() {
        switch setupScreenState {
        case .NotStarted, .Failed:
            doSetupUseCase.invoke { setupState in
                if let inProgress  = setupState as? SetupState.InProgress {
                    DispatchQueue.main.async {
                        self.setupScreenState =
                            .InProgress(progress: Float(inProgress.progress))
                    }
                }
                
                if let _  = setupState as? SetupState.Complete {
                    DispatchQueue.main.async {
                        self.setupScreenState = .Complete
                    }
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
