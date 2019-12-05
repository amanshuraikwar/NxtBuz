package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String,
    var name: String,
    var email: String,
    val userPicUrl: String?
) : Parcelable