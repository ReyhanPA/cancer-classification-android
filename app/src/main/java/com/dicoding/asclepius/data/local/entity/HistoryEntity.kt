package com.dicoding.asclepius.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "image_uri")
    var imageUri: Uri? = null

    @ColumnInfo(name = "result")
    var result: String? = null

    @ColumnInfo(name = "inference_time")
    var inferenceTime: String? = null
}