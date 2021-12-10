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
            entity = Course::class,
            parentColumns = ["course_id"],
            childColumns = ["module_course_id"],
            onDelete = CASCADE
        )
    ]
)
data class Module(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "module_id")
    val id: Int = 0,
    @ColumnInfo(name = "module_course_id")
    var course_id: Int,
    @ColumnInfo(name = "module_name")
    var name: String,
    @ColumnInfo(name = "module_number")
    var module_number: Int,
    @ColumnInfo(name = "number_of_lessons")
    var number_of_lessons: Int

) : Serializable