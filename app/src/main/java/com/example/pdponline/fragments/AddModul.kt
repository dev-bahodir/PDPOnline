package com.example.pdponline.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.R
import com.example.pdponline.adapters.ModuleAdapter
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentAddModulBinding
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Module
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStream

class AddModul : Fragment(R.layout.fragment_add_modul) {
    private val binding by viewBinding(FragmentAddModulBinding::bind)
    lateinit var appDatabase: AppDatabase
    private lateinit var moduleAdapter: ModuleAdapter
    lateinit var course: Course
    private lateinit var listModules: ArrayList<Module>

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        course = arguments?.getSerializable("course") as Course
        binding.courseName.isSelected = true

        binding.apply {
            courseName.text = course.name
            addModule.setOnClickListener {
                val name = moduleName.text.toString()
                val number = moduleNumber.text.toString()
                when {
                    name == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Enter module name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    number == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Enter module ordinal number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        val module = Module(
                            name = name,
                            course_id = course.id,
                            module_number = Integer.parseInt(number),
                            number_of_lessons = 0
                        )
                        appDatabase.pdpDao().addModule(module)
                        binding.moduleName.setText("")
                        binding.moduleNumber.setText("")
                    }
                }
            }

        }
        moduleAdapter = ModuleAdapter(object : ModuleAdapter.OnItemClickListener {
            override fun onEditClick(module: Module) {
                val bundle = Bundle()
                bundle.putSerializable("module", module)
                findNavController().navigate(R.id.action_addModul_to_editModul, bundle)
            }

            override fun onDeleteClick(module: Module) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Lessons are included in this module. Do you agree that it should be deleted along with the lessons?")
                builder.setPositiveButton("Yes"
                ) { _, _ -> appDatabase.pdpDao().deleteModule(module) }
                builder.setNegativeButton("No"
                ) { _, _ -> builder.create().dismiss() }
                builder.show()
            }

            override fun getInputStream(position: Int, module: Module): InputStream {
                return requireActivity().contentResolver.openInputStream(Uri.parse(course.pictureUriToString))!!
            }

            override fun onItemClick(module: Module) {
                val bundle = Bundle()
                bundle.putSerializable("module", module)
                bundle.putSerializable("course", course)
                findNavController().navigate(
                    R.id.action_addModul_to_addLesson,
                    bundle
                )
            }

        }, course)
        binding.rv.adapter = moduleAdapter

        appDatabase.pdpDao()
            .getModules()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it ->
                listModules = ArrayList(it.filter {
                    it.course_id == course.id
                })
                moduleAdapter.submitList(listModules)
            }, {

            }) {

            }
    }
}