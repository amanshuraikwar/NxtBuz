//
//  UseCaseResult.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 22/10/21.
//

import Foundation

enum UseCaseResult<T> {
    case Success(data: T)
    case Error(message: String)
}

