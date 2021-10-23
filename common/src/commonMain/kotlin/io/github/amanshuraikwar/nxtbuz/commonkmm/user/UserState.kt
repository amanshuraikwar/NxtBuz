package io.github.amanshuraikwar.nxtbuz.commonkmm.user

sealed class UserState {
    object New : UserState()
    object SetupComplete : UserState()
}