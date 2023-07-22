package com.luojunkai.app.general

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class general(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var imageResource: Int,
    var title: String,
    var content: String,
    var iconResource: Int,
    var label: String,
    var source: String,
    var imageUrl: String
)