package com.example.pdponline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdponline.databinding.ItemLesson2Binding
import com.example.pdponline.entities.Lesson


class LessonsByModuleAdapter(var onItemClickListener: OnItemClickListener) :
    ListAdapter<Lesson, LessonsByModuleAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem
        }

    }

    inner class Vh(private var item_lesson2: ItemLesson2Binding) :
        RecyclerView.ViewHolder(item_lesson2.root) {
        fun onBind(lesson: Lesson) {

            item_lesson2.apply {

                item_lesson2.numberOfLesson.text = lesson.number.toString()
                item_lesson2.shadowBtn.button.setOnClickListener {
                    onItemClickListener.onButtonClick(lesson)
                }
            }
        }
    }


    interface OnItemClickListener {
        fun onButtonClick(lesson: Lesson)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemLesson2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }


}