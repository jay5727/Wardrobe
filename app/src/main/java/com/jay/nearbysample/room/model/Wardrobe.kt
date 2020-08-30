package com.jay.nearbysample.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for holding Wardrobe
 */
@Entity(tableName = "wardrobe_table")
class Wardrobe(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray,
    var type: String?
)