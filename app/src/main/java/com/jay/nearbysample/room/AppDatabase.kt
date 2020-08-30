package com.jay.nearbysample.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jay.nearbysample.room.dao.LikedDao
import com.jay.nearbysample.room.model.Wardrobe
import com.jay.nearbysample.room.dao.WardrobeDao
import com.jay.nearbysample.room.model.Liked

/**
 * AppDatabase class to handle CRUD related operations
 * Include the list of entities associated with the database within the annotation.
 * Contains Abstract DAO methods
 */
@Database(entities = [Wardrobe::class,Liked::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getWardrobeDao(): WardrobeDao

    abstract fun getLikedDao(): LikedDao

    companion object {
        const val DATABASE_NAME = "NearBy.db"
    }

}