package com.example.pdponline.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdponline.databinding.ItemModuleBinding
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Module
import java.io.InputStream


class ModuleAdapter(var onItemClickListener: OnItemClickListener, var course: Course) :
    ListAdapter<Module, ModuleAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<Module>() {
        override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem == newItem
        }

    }

    inner class Vh(private var itemModuleBinding: ItemModuleBinding) :
        RecyclerView.ViewHolder(itemModuleBinding.root) {
        fun onBind(module: Module, position: Int) {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(module)
            }
            itemModuleBinding.apply {

                itemModuleBinding.moduleName.isSelected = true
                itemModuleBinding.moduleName.text = module.name
                itemModuleBinding.moduleNumber.text = module.module_number.toString()
                itemModuleBinding.courseLogo.setImageURI(Uri.parse(course.pictureUriToString))

                itemModuleBinding.edit.setOnClickListener {
                    onItemClickListener.onEditClick(getItem(position))
                }
                itemModuleBinding.delete.setOnClickListener {
                    onItemClickListener.onDeleteClick(getItem(position))
                }
            }
        }
    }


    interface OnItemClickListener {
        fun onEditClick(module: Module)

        fun onDeleteClick(module: Module)

        fun getInputStream(position: Int, module: Module): InputStream

        fun onItemClick(module: Module)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position), position)
    }


}