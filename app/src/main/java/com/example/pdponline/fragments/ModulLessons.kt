package com.example.pdponline.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.R
import com.example.pdponline.adapters.LessonsByModuleAdapter
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentModulLessonsBinding
import com.example.pdponline.entities.Lesson
import com.example.pdponline.entities.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ModulLessons : Fragment(R.layout.fragment_modul_lessons) {
    private val binding by viewBinding(FragmentModulLessonsBinding::bind)
    lateinit var appDatabase: AppDatabase
    lateinit var module: Module
    private lateinit var lessonsByModuleAdapter: LessonsByModuleAdapter


    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        module = arguments?.getSerializable("module") as Module

        binding.apply {
            moduleName.isSelected = true
            moduleName.text = module.name
        }
        lessonsByModuleAdapter =
            LessonsByModuleAdapter(object : LessonsByModuleAdapter.OnItemClickListener {
                override fun onButtonClick(lesson: Lesson) {
                    val bundle = Bundle()
                    bundle.putSerializable("lesson", lesson)
                    findNavController().navigate(R.id.action_modulLessons_to_lessonData, bundle)
                }
            })
        binding.rv.adapter = lessonsByModuleAdapter
        appDatabase.pdpDao()
            .getLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                val lessonList = ArrayList(it.filter {
                    it.module_id == module.id
                })
                lessonsByModuleAdapter.submitList(lessonList)
            }, {

            }) {

            }
    }
}