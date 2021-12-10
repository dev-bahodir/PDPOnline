package com.example.pdponline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdponline.databinding.ItemModule2Binding
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Module


class ModuleLessonsAdapter(var onItemClickListener: OnItemClickListener, var course: Course) :
    ListAdapter<Module, ModuleLessonsAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<Module>() {
        override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem == newItem
        }

    }

    inner class Vh(var item_course_modules: ItemModule2Binding) :
        RecyclerView.ViewHolder(item_course_modules.root) {
        fun onBind(module: Module, position: Int) {

            item_course_modules.apply {

                item_course_modules.courseName.text = course.name
                item_course_modules.moduleName.text = module.name
                item_course_modules.numberOfLessons.text = module.number_of_lessons.toString()

                item_course_modules.more.setOnClickListener {
                    onItemClickListener.onMoreClick(module)
                }
            }
        }
    }


    interface OnItemClickListener {
        fun onMoreClick(module: Module)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemModule2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position), position)
    }


}