package com.example.pdponline.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Module::class,
            parentColumns = ["module_id"],
            childColumns = ["lesson_module_id"],
            onDelete = CASCADE
        )
    ]
)
data class Lesson(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "lesson_id")
    val id: Int = 0,
    @ColumnInfo(name = "lesson_name")
    var name: String,
    @ColumnInfo(name = "lesson_module_id")
    var module_id: Int,
    @ColumnInfo(name = "lesson_data")
    var data: String,
    @ColumnInfo(name = "lesson_number")
    var number: Int

) : Serializable