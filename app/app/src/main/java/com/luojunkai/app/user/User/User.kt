package com.luojunkai.app.user.User

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // 将默认值改为 0
    var avatar: Int?, // 头像图片资源ID，使用可空类型，允许为空
    var avatarUrl: String? = null, // 头像图片URL，使用可空类型，允许为空
    var nickname: String = "" // 昵称，使用空字符串作为默认值
)