package com.example.pdponline.dao

import androidx.room.*
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Lesson
import com.example.pdponline.entities.Module
import io.reactivex.Flowable


@Dao
interface PDPDao {

    @Insert
    fun addCourse(course: Course)

    @Insert
    fun addModule(module: Module)

    @Insert
    fun addLesson(lesson: Lesson)

    @Update
    fun editCourse(course: Course)

    @Update
    fun editModule(module: Module)

    @Update
    fun editLesson(lesson: Lesson)

    @Delete
    fun deleteCourse(course: Course)

    @Delete
    fun deleteModule(module: Module)

    @Delete
    fun deleteLesson(lesson: Lesson)

    @Query("select *from course")
    fun getCourses(): Flowable<List<Course>>

    @Query("select * from module order by module_number")
    fun getModules(): Flowable<List<Module>>

    @Query("select * from lesson order by lesson_number")
    fun getLessons(): Flowable<List<Lesson>>

}