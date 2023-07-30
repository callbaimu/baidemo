package com.luojunkai.app.home.general

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class general(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,
    var content: String,
    var iconResource: Int,
    var label: String,
    var source: String,
    var imageUrl: String
)