package com.example.pdponline.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Course(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    val id: Int = 0,
    @ColumnInfo(name = "course_name")
    var name: String,
    @ColumnInfo(name = "course_picture_uri")
    var pictureUriToString: String

    ):Serializable