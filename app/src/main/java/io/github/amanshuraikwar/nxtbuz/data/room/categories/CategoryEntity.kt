package io.github.amanshuraikwar.nxtbuz.data.room.categories

import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class CategoryEntity(
    val id: String,
    val name: String,
    val monthlyLimit: Double
)