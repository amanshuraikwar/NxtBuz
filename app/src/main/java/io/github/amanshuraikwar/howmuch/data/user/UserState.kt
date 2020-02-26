package io.github.amanshuraikwar.howmuch.data.user

sealed class UserState {
    object New : UserState()
    object SetupComplete : UserState()
}