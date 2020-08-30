package com.jay.nearbysample.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for holding Liked
 */
@Entity(tableName = "liked_table")
class Liked(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var likedID: String? = null
)
