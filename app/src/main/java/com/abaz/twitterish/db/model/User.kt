package com.abaz.twitterish.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * @author: Anthony Busto
 * @date:   2019-07-27
 */
@Entity(tableName = "User")
data class User(
    @ColumnInfo(name = "ID")val id: Long,
    @ColumnInfo(name = "USER_NAME") val username: String,
    @ColumnInfo(name = "FIRST_NAME") val firstName: String?,
    @ColumnInfo(name = "LAST_NAME") val lastName: String?,
    @ColumnInfo(name = "AGE") val age: Int?,
    val jwt: String?
)