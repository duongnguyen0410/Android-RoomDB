package com.example.roomdemo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
@Dao
interface SubscriberDAO {
    @Insert
    suspend fun subscriberInsert(subscriber: Subscriber): Long

    @Update
    suspend fun subscriberUpdate(subscriber: Subscriber): Int

    @Delete
    suspend fun subscriberDelete(subscriber: Subscriber): Int

    @Query("DELETE FROM subscriber_db")
    suspend fun subscriberDeleteAll(): Int

    @Query("SELECT * FROM subscriber_db")
    fun subscriberGetAll(): LiveData<List<Subscriber>>
}