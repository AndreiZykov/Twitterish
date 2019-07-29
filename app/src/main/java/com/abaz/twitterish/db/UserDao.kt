package com.abaz.twitterish.db

import androidx.room.Delete
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abaz.twitterish.data.User


/**
 * @author: Anthony Busto
 * @date:   2019-07-29
 */

@Dao
interface UserDao {

    @get:Query("SELECT * FROM user")
    val all: List<User>

    @Query("SELECT * FROM user where FIRST_NAME LIKE :firstName AND LAST_NAME LIKE :lastName")
    fun findByName(firstName: String, lastName: String): User

    @Query("SELECT COUNT(*) from user")
    fun countUsers(): Int

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}