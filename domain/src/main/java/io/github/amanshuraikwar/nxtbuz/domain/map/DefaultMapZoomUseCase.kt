package io.github.amanshuraikwar.nxtbuz.domain.map

import javax.inject.Inject

class DefaultMapZoomUseCase @Inject constructor(
) {
    operator fun invoke(): Float {
        return 16f
    }
}