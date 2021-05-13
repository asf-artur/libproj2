package com.example.libproj2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.libproj2.models.User
import io.reactivex.rxjava3.core.Single

@Dao
interface UserDao {
    @Query("SELECT * from User LIMIT 1")
    fun get() : LiveData<User>

    @Query("SELECT * from User where id = 199")
    fun getRx() : Single<User>

    @Insert
    fun add(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE from User")
    fun deleteAll()
}