package com.jay.nearbysample.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jay.nearbysample.room.model.Liked
import io.reactivex.Observable

@Dao
interface LikedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: Liked)

    @Query("SELECT * FROM liked_table")
    fun getLikedList(): Observable<List<Liked>>

    @Query("DELETE FROM liked_table WHERE likedID = :likedId")
    suspend fun deleteById(likedId: String)
}