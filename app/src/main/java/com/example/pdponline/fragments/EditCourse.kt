package com.example.pdponline.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.BuildConfig
import com.example.pdponline.R
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.databinding.FragmentAddLessonBinding
import com.example.pdponline.databinding.FragmentEditCourseBinding
import com.example.pdponline.entities.Course
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class EditCourse : Fragment(R.layout.fragment_edit_course) {
    private val binding by viewBinding(FragmentEditCourseBinding::bind)
    lateinit var course: Course
    lateinit var currentPhotoPath: String
    lateinit var currentPhotoPath1: Uri
    lateinit var appDatabase: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameOfCourse.isSelected = true
        appDatabase = AppDatabase.getInstance(requireContext())

        binding.apply {
            course = arguments?.getSerializable("course") as Course

            currentPhotoPath1 = Uri.parse(course.pictureUriToString)
            binding.picture.setImageURI(currentPhotoPath1)
            picture.setOnClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setMessage("Which of the following do you use to upload a picture?")
                dialog.setPositiveButton("Camera", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        perMission()
                    }
                })
                dialog.setNegativeButton("Gallery", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        permission1()
                    }
                })
                dialog.show()
            }
        }
//        binding.picture.setImageURI(Uri.parse(course.pictureUriToString))
        binding.nameOfCourse.setText(course.name)
        binding.name.setText(course.name)

        binding.editCourse.setOnClickListener {
            when {
                binding.name.text.toString() == "" -> {
                    Toast.makeText(requireContext(), "Enter course name", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    course.name = binding.name.text.toString()
                    course.pictureUriToString = currentPhotoPath1.toString()
                    appDatabase.pdpDao().editCourse(course)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun permission1() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    newMethodGallery()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response != null) {
                        if (response.isPermanentlyDenied) {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri =
                                Uri.fromParts(
                                    "package",
                                    requireContext().packageName,
                                    null
                                )
                            intent.data = uri
                            startActivity(intent)
                        } else {
                            permission1()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    token: PermissionToken?
                ) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Ask for permission")
                    builder.setMessage("Do you allow this app to be used in a gallery?")
                    builder.setPositiveButton("Yes", fun(p0: DialogInterface, p1: Int) {
                        token?.continuePermissionRequest()
                    })
                    builder.setNegativeButton(
                        "No"
                    ) { _, _ -> android.os.Process.killProcess(android.os.Process.myPid()) }
                    builder.show()
                }
            }).check()
    }

    private fun newMethodGallery() {
        getImageContent.launch("image/*")
    }

    val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult
        binding.picture.setImageURI(uri)
        currentPhotoPath1 = uri
        val openInputStream = requireContext().contentResolver.openInputStream(uri)
        val m = System.currentTimeMillis()
        val file = File(requireActivity().filesDir, "$m.jpg")
        val fileOutputStream = FileOutputStream(file)
        openInputStream?.copyTo(fileOutputStream)
        openInputStream?.close()
        fileOutputStream.close()
    }


    private fun perMission() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    newMethodCamera()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response != null) {
                        if (response.isPermanentlyDenied) {
                            var intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            var uri: Uri =
                                Uri.fromParts(
                                    "package",
                                    requireContext().packageName,
                                    null
                                )
                            intent.data = uri
                            startActivity(intent)
                        } else {
                            perMission()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    token: PermissionToken?
                ) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Ask for permission")
                    builder.setMessage("Do you allow this app to be used in a camera?")
                    builder.setPositiveButton("Yes", fun(p0: DialogInterface, p1: Int) {
                        token?.continuePermissionRequest()
                    })
                    builder.setNegativeButton(
                        "No"
                    ) { p0, p1 -> android.os.Process.killProcess(android.os.Process.myPid()) }
                    builder.show()
                }
            }).check()
    }

    private fun newMethodCamera() {
        val photoFile = try {
            createImageFile()
        } catch (e: Exception) {
            null
        }
        photoFile?.also {
            val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, it)
            getCameraImage.launch(uri)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val m = System.currentTimeMillis()
        val externalFilesDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("G22_$m", ".jpg", externalFilesDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            binding.picture.setImageURI(Uri.fromFile(File(currentPhotoPath)))
            currentPhotoPath1 = Uri.fromFile(File(currentPhotoPath))
        }
    }

}

