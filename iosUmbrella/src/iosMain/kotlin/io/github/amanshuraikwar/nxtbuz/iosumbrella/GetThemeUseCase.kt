package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.dynamo.DynamoTheme
import io.github.amanshuraikwar.dynamo.DynamoThemeRepository
import io.github.amanshuraikwar.nxtbuz.domain.fromFlow
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult

class GetThemeUseCase(
    private val dynamoThemeRepository: DynamoThemeRepository
) {
    fun getThemeSync(): DynamoTheme {
        return dynamoThemeRepository.getThemeData()
    }

    fun getThemeUpdates(callback: (IosResult<DynamoTheme>) -> Unit) {
        callback fromFlow {
            dynamoThemeRepository.getThemeDataFlow()
        }
    }
}