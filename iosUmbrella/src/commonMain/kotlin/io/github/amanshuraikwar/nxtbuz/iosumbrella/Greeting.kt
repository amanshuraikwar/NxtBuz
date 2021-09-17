package io.github.amanshuraikwar.nxtbuz.iosumbrella

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}