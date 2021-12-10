package com.example.pdponline.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.databinding.FragmentHomeScreenBinding
import com.dagang.library.GradientButton
import com.example.pdponline.R
import com.example.pdponline.adapters.HomeCourseAdapter
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class HomeScreen : Fragment(R.layout.fragment_home_screen) {
    private val binding by viewBinding(FragmentHomeScreenBinding::bind)
    lateinit var appDatabase: AppDatabase
    lateinit var homeCourseAdapter: HomeCourseAdapter


    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreen_to_addCourse)
        }

        homeCourseAdapter = HomeCourseAdapter(object : HomeCourseAdapter.OnItemClickListener {
            override fun onAllClick(course: Course) {
                val bundle = Bundle()
                bundle.putSerializable("course", course)
                findNavController().navigate(R.id.action_homeScreen_to_courseModuls, bundle)
            }

            override fun onModuleClick(module: Module) {
                val bundle = Bundle()
                bundle.putSerializable("module", module)
                findNavController().navigate(
                    R.id.action_homeScreen_to_modulLessons, bundle
                )
            }

        }, appDatabase)
        binding.rvVertical.adapter = homeCourseAdapter

        appDatabase.pdpDao()
            .getCourses()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                homeCourseAdapter.submitList(it)
            }, {

            }) {

            }

    }

}