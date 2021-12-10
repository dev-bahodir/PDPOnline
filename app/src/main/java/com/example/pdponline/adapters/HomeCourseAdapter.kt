package com.example.pdponline.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.ItemHomeCourseBinding
import com.example.pdponline.databinding.ItemHomeModuleBinding
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeCourseAdapter(
    var onItemClickListener: OnItemClickListener,
    var appDatabase: AppDatabase
) :
    ListAdapter<Course, HomeCourseAdapter.Vh>(MyDiffUtil()) {
    lateinit var insideAdapter: InsideAdapter
    lateinit var listModules: ArrayList<Module>

    class MyDiffUtil : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }

    inner class Vh(private var item_home_course_binding: ItemHomeCourseBinding) :
        RecyclerView.ViewHolder(item_home_course_binding.root) {
        @SuppressLint("CheckResult")
        fun onBind(course: Course) {

            item_home_course_binding.apply {
                courseName.text = course.name

                all.setOnClickListener {
                    onItemClickListener.onAllClick(course)
                }

                appDatabase.pdpDao()
                    .getModules()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it ->
                        listModules = ArrayList(it.filter {
                            it.course_id == course.id
                        })
                        insideAdapter = InsideAdapter(listModules, onItemClickListener)
                        rv.adapter = insideAdapter
                    }, {

                    }) {

                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemHomeCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))

    }

    class InsideAdapter(var list: ArrayList<Module>, var onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<InsideAdapter.Vh>() {

        inner class Vh(var itemHomeBinding: ItemHomeModuleBinding) :
            RecyclerView.ViewHolder(itemHomeBinding.root) {
            fun onBind(module: Module) {
                itemHomeBinding.apply {
                    itemHomeBinding.moduleName.text = module.name
                    itemHomeBinding.moduleBtn.button.setOnClickListener {
                        onItemClickListener.onModuleClick(module)
                    }
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
            return Vh(
                ItemHomeModuleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: Vh, position: Int) {
            holder.onBind(list[position])
        }

        override fun getItemCount(): Int = list.size


    }

    interface OnItemClickListener {
        fun onAllClick(course: Course)

        fun onModuleClick(module: Module)
    }
}