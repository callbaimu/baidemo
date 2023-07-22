package com.luojunkai.app.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 1, // 使用自动生成的主键，确保每个用户有唯一的ID
    var avatar: Int?, // 头像图片资源ID，使用可空类型，允许为空
    var avatarUrl: String? = null, // 头像图片URL，使用可空类型，允许为空
    var nickname: String = "" // 昵称，使用空字符串作为默认值
)
