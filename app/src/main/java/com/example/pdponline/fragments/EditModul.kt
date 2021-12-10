package com.example.pdponline.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.R
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentEditModulBinding
import com.example.pdponline.entities.Module

class EditModul : Fragment(R.layout.fragment_edit_modul) {
    private val binding by viewBinding(FragmentEditModulBinding::bind)
    lateinit var appDatabase: AppDatabase
    lateinit var module: Module

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        module = arguments?.getSerializable("module") as Module
        binding.moduleName.isSelected = true
        binding.moduleName.text = module.name
        binding.moduleNameEdit.setText(module.name)
        binding.moduleNumberTv.setText(module.module_number.toString())
        binding.editModule.setOnClickListener {
            when {
                binding.moduleNameEdit.text.toString().trim() == "" -> {
                    Toast.makeText(requireContext(), "Enter module name", Toast.LENGTH_SHORT)
                        .show()
                }
                binding.moduleNumberTv.text.toString().trim() == "" -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter module ordinal number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    module.name = binding.moduleNameEdit.text.toString()
                    module.module_number = Integer.parseInt(binding.moduleNumberTv.text.toString())
                    appDatabase.pdpDao().editModule(module)
                    findNavController().popBackStack()
                }
            }
        }


    }

}