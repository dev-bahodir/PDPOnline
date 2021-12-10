package com.example.pdponline.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdponline.databinding.ItemLessonBinding
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Lesson
import java.io.InputStream


class LessonAdapter(var onItemClickListener: OnItemClickListener, var course: Course) :
    ListAdapter<Lesson, LessonAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem
        }
    }

    inner class Vh(var itemLessonBinding: ItemLessonBinding) :
        RecyclerView.ViewHolder(itemLessonBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(lesson: Lesson, position: Int) {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(lesson)
            }
            itemLessonBinding.apply {

                itemLessonBinding.lessonName.text = lesson.name
                itemLessonBinding.numberOfLesson.text = lesson.number.toString() + " - dars"
                itemLessonBinding.courseLogo.setImageURI(Uri.parse(course.pictureUriToString))

                itemLessonBinding.edit.setOnClickListener {
                    onItemClickListener.onEditClick(getItem(position))
                }
                itemLessonBinding.delete.setOnClickListener {
                    onItemClickListener.onDeleteClick(getItem(position))
                }
            }
        }
    }
    interface OnItemClickListener {
        fun onEditClick(lesson: Lesson)

        fun onDeleteClick(lesson: Lesson)

        fun getInputStream(position: Int, lesson: Lesson): InputStream

        fun onItemClick(lesson: Lesson)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position), position)
    }
}