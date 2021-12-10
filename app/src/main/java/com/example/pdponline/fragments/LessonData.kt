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
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentAddLessonBinding
import com.example.pdponline.databinding.FragmentLessonDataBinding
import com.example.pdponline.entities.Lesson

class LessonData : Fragment(R.layout.fragment_lesson_data) {

    private val binding by viewBinding(FragmentLessonDataBinding::bind)
    private lateinit var appDatabase: AppDatabase
    lateinit var lesson: Lesson

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        lesson = arguments?.getSerializable("lesson") as Lesson
        binding.apply {
            popBackStack.setOnClickListener {
                findNavController().popBackStack()
            }
            numberOfLesson.text = lesson.number.toString() + " - lesson"
            nameOfLesson.text = lesson.name
            lessonData.text = lesson.data
        }

    }

}