package com.jay.nearbysample.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jay.nearbysample.enums.WardrobeType
import com.jay.nearbysample.room.model.Wardrobe
import io.reactivex.Observable

@Dao
interface WardrobeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Wardrobe) : Long

    @Query("SELECT * FROM wardrobe_table")
    fun getAllWardrobe(): Observable<List<Wardrobe>>

    @Query("SELECT * FROM wardrobe_table WHERE type = :type")
    fun getShirtWardrobe(type: String = WardrobeType.SHIRT.type): Observable<List<Wardrobe>>

    @Query("SELECT * FROM wardrobe_table WHERE type = :type")
    fun getJeansWardrobe(type: String = WardrobeType.JEANS.type): Observable<List<Wardrobe>>
}