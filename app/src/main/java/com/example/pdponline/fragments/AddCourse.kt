package com.example.pdponline.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pdponline.R
import com.example.pdponline.databinding.FragmentAddCourseBinding
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.pdponline.BuildConfig
import com.example.pdponline.adapters.CoursesAdapter
import com.example.pdponline.adapters.HomeCourseAdapter
import com.example.pdponline.database.AppDatabase
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Module
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


class AddCourse : Fragment(R.layout.fragment_add_course) {
    private val binding by viewBinding(FragmentAddCourseBinding::bind)
    lateinit var appDatabase: AppDatabase
    var currentPhotoPath: String = ""
    var currentPhotoPath1: Uri? = null
    val REQUEST_CODE = 1
    lateinit var courses: Flowable<List<Course>>
    lateinit var coursesAdapter: CoursesAdapter
    private lateinit var fileAbsolutePath: String
    lateinit var listModules: ArrayList<Module>
    lateinit var insideAdapter: HomeCourseAdapter.InsideAdapter


    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabase = AppDatabase.getInstance(requireContext())

        binding.apply {

            addCourse.setOnClickListener {
                val courseName = name.text.toString()
                when {
                    courseName == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Enter course name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    currentPhotoPath1 == null -> {
                        Toast.makeText(
                            requireContext(),
                            "No picture was selected for course",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        val course = Course(
                            name = courseName,
                            pictureUriToString = currentPhotoPath1.toString()
                        )
                        appDatabase.pdpDao().addCourse(course)
                        name.setText("")
                        picture.setImageResource(R.drawable.placeholder_image)
                    }
                }
            }
            picture.setOnClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("Which of the following do you use to upload a picture?")
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
        coursesAdapter = CoursesAdapter(object : CoursesAdapter.OnItemClickListener {
            override fun onEditClick(course: Course) {
                val bundle = Bundle()
                courses = appDatabase.pdpDao().getCourses()
                bundle.putSerializable("course", course)
                findNavController().navigate(R.id.action_addCourse_to_editCourse, bundle)
            }

            override fun onDeleteClick(course: Course) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Modules are included in this course. Do you agree that it will be deleted along with the modules?")
                builder.setPositiveButton(
                    "Yes"
                ) { _, _ -> appDatabase.pdpDao().deleteCourse(course) }
                builder.setNegativeButton(
                    "No"
                ) { _, _ -> builder.create().dismiss() }
                builder.show()
            }

            override fun getInputStream(position: Int, course: Course): InputStream {
                return requireActivity().contentResolver.openInputStream(Uri.parse(course.pictureUriToString))!!
            }

            override fun onItemClick(course: Course) {
                val bundle = Bundle()
                bundle.putSerializable("course", course)
                findNavController().navigate(R.id.action_addCourse_to_addModul, bundle)
            }
        })

        binding.rv.adapter = coursesAdapter

        appDatabase.pdpDao()
            .getCourses()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                coursesAdapter.submitList(it)
            }, {

            }) {

            }
    }

    private fun permission1() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    oldMethodGallery()
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
        fileAbsolutePath = file.absolutePath
        val readBytes = file.readBytes()

    }

    private fun oldMethodGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val uri = data?.data ?: return
            currentPhotoPath1=uri
            binding.picture.setImageURI(uri)
            val openInputStream = requireContext().contentResolver.openInputStream(uri)
            val m = System.currentTimeMillis()
            val file = File(requireActivity().filesDir, "$m.jpg")
            val fileOutputStream = FileOutputStream(file)
            openInputStream?.copyTo(fileOutputStream)
            openInputStream?.close()
            fileOutputStream.close()

        }
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