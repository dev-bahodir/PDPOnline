package com.example.pdponline.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.R
import com.example.pdponline.adapters.LessonAdapter
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentAddLessonBinding
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Lesson
import com.example.pdponline.entities.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStream

@Suppress("LocalVariableName")
class AddLesson : Fragment(R.layout.fragment_add_lesson) {
    private val binding by viewBinding(FragmentAddLessonBinding::bind)
    lateinit var module: Module
    lateinit var appDatabase: AppDatabase
    private lateinit var lessonAdapter: LessonAdapter
    lateinit var course: Course
    private lateinit var listLessons: ArrayList<Lesson>

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())


        module = arguments?.getSerializable("module") as Module
        course = requireArguments().getSerializable("course") as Course
        binding.moduleName.isSelected = true
        binding.apply {
            moduleName.text = module.name

            addLesson.setOnClickListener {
                when {
                    lessonName.text.toString().trim() == "" -> {
                        Toast.makeText(requireContext(), "Enter lesson name", Toast.LENGTH_SHORT)
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
                    lessonData.text.toString().trim() == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Enter lesson information",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> {
                        val name = lessonName.text.toString().trim()
                        val number_of_lesson = lessonNumber.text.toString().trim()
                        val data = lessonData.text.toString().trim()
                        val lessons =
                            Lesson(
                                name = name,
                                number = Integer.parseInt(number_of_lesson),
                                data = data,
                                module_id = module.id
                            )
                        appDatabase.pdpDao().addLesson(lessons)
                        module.number_of_lessons += 1
                        appDatabase.pdpDao().editModule(module)
                        lessonName.setText("")
                        lessonData.setText("")
                        lessonNumber.setText("")
                        Toast.makeText(requireContext(), "Lesson was successfully added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        lessonAdapter = LessonAdapter(object : LessonAdapter.OnItemClickListener {
            override fun onEditClick(lesson: Lesson) {
                val bundle = Bundle()
                bundle.putSerializable("lesson", lesson)
                findNavController().navigate(R.id.action_addLesson_to_editLesson, bundle)
            }

            override fun onDeleteClick(lesson: Lesson) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Do you agree to cancel classes?")
                builder.setPositiveButton("Yes"
                ) { _, _ ->
                    appDatabase.pdpDao().deleteLesson(lesson)
                    Toast.makeText(requireContext(), "Lesson was successfully deleted!", Toast.LENGTH_SHORT)
                        .show()
                }
                builder.setNegativeButton("No"
                ) { _, _ -> builder.create().dismiss() }
                builder.show()
            }

            override fun getInputStream(position: Int, lesson: Lesson): InputStream {
                return requireActivity().contentResolver.openInputStream(Uri.parse(course.pictureUriToString))!!
            }

            override fun onItemClick(lesson: Lesson) {
                val bundle = Bundle()
                bundle.putSerializable("lesson", lesson)
                findNavController().navigate(R.id.lessonData, bundle)
            }
        }, course)

        binding.rv.adapter = lessonAdapter
        appDatabase.pdpDao()
            .getLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                listLessons = ArrayList(it.filter {
                    it.module_id == module.id
                })
                lessonAdapter.submitList(listLessons)
            }, {

            }) {

            }
    }

}