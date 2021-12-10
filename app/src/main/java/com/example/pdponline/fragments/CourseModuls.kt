package com.example.pdponline.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.R
import com.example.pdponline.adapters.CourseModulesAdapter
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentAddLessonBinding
import com.example.pdponline.databinding.FragmentCourseModulsBinding
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CourseModuls : Fragment(R.layout.fragment_course_moduls) {
    private val binding by viewBinding(FragmentCourseModulsBinding::bind)
    lateinit var course: Course
    lateinit var courseModulesAdapter: CourseModulesAdapter
    lateinit var appDatabase: AppDatabase

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())

        binding.courseName.isSelected = true
        course = arguments?.getSerializable("course") as Course
        binding.apply {
            courseName.text = course.name

            courseModulesAdapter =
                CourseModulesAdapter(object : CourseModulesAdapter.OnItemClickListener {
                    override fun onMoreClick(module: Module) {
                        val bundle = Bundle()
                        bundle.putSerializable("module", module)
                        findNavController().navigate(
                            R.id.action_courseModuls_to_modulLessons,
                            bundle
                        )
                    }
                }, course)
            rv.adapter = courseModulesAdapter
        }

        appDatabase.pdpDao()
            .getModules()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                val listModules = ArrayList(it.filter {
                    it.course_id == course.id
                })
                courseModulesAdapter.submitList(listModules)
            }, {

            }) {

            }
    }
}