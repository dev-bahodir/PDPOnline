package com.example.pdponline.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.R
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentEditLessonBinding
import com.example.pdponline.entities.Lesson

class EditLesson : Fragment(R.layout.fragment_edit_lesson) {
    private val binding by viewBinding(FragmentEditLessonBinding::bind)
    lateinit var appDatabase: AppDatabase
    private lateinit var lesson: Lesson

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        lesson = arguments?.getSerializable("lesson") as Lesson

        binding.apply {
            numberOfLesson.text = lesson.number.toString() + " - lesson"

            lessonName.setText(lesson.name)
            lessonData.setText(lesson.data)
            lessonNumber.setText(lesson.number.toString())

            editLesson.setOnClickListener {
                when {
                    lessonName.text.toString().trim() == "" -> {
                        Toast.makeText(requireContext(), "Enter lesson name", Toast.LENGTH_SHORT)
                            .show()
                    }
                    lessonData.text.toString().trim() == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Enter lesson information",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    lessonNumber.text.toString().trim() == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Enter lesson ordinal number",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> {
                        lesson.name = lessonName.text.toString().trim()
                        lesson.data = lessonData.text.toString().trim()
                        lesson.number = Integer.parseInt(lessonNumber.text.toString().trim())
                        appDatabase.pdpDao().editLesson(lesson)
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Lesson was successfully edited!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}