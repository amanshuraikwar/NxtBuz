package io.github.amanshuraikwar.nxtbuz.domain.map

class DefaultMapZoomUseCase constructor() {
    operator fun invoke(): Float {
        return 16f
    }
}