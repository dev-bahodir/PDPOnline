package com.example.pdponline.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdponline.databinding.ItemCourseBinding
import com.example.pdponline.entities.Course
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import java.io.InputStream


class CoursesAdapter(var onItemClickListener: OnItemClickListener) :
    ListAdapter<Course, CoursesAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }

    inner class Vh(var itemCourseBinding: ItemCourseBinding) :
        RecyclerView.ViewHolder(itemCourseBinding.root) {
        fun onBind(course: Course, position: Int) {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(course)
            }
            itemCourseBinding.apply {
                courseName.isSelected = true
                courseName.text = course.name
                courseLogo.setImageURI(Uri.parse(getItem(position).pictureUriToString))

                itemCourseBinding.edit.setOnClickListener {
                    onItemClickListener.onEditClick(getItem(position))
                }
                itemCourseBinding.delete.setOnClickListener {
                    onItemClickListener.onDeleteClick(getItem(position))
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onEditClick(course: Course)

        fun onDeleteClick(course: Course)

        fun getInputStream(position: Int, course: Course): InputStream

        fun onItemClick(course: Course)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position), position)
    }
}