//
//  Util.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 22/10/21.
//

import Foundation
import iosUmbrella

class Util {
    public static func toUseCaseResult<T>(_ iosResult: IosResult<T>) -> UseCaseResult<T> {
        if let success = iosResult as? IosResultSuccess {
            return UseCaseResult.Success(data: success.data!)
        }
        
        if let error = iosResult as? IosResultError {
            return UseCaseResult.Error(message: error.errorMessage)
        }
        
        return UseCaseResult.Error(message: "Unexpected IosResult value")
    }

    public static func onMain(predicate: () -> ()) {
        DispatchQueue.main.sync {
            predicate()
        }
    }
}
