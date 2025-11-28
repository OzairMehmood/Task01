package com.example.task01

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.task01.api.RetrofitClient
import com.example.task01.databinding.ActivityMainBinding
import com.example.task01.dataclasses.StudentResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null

    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Select Image
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        // DOB Datepicker
        binding.etDOB.setOnClickListener {
            openDatePicker()
        }

        // Upload Student
        binding.btnSubmit.setOnClickListener {
            uploadStudent()
        }

        // Show Students
        binding.btnViewStudents.setOnClickListener {
            val intent = Intent(this, ShowStudentActivity::class.java)
            startActivity(intent)
        }

    }

    // Handle gallery result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data?.data
            binding.imagePreview.setImageURI(imageUri)
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dp = DatePickerDialog(this, { _, y, m, d ->
            binding.etDOB.setText("$y-${m + 1}-$d")
        }, year, month, day)

        dp.show()
    }

    private fun uploadStudent() {
        val name = binding.etName.text.toString().trim()
        val dob = binding.etDOB.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()

        if (name.isEmpty() || dob.isEmpty() || gender.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val filePath = getPathFromUri(imageUri!!)
        val file = File(filePath)

        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val multipart = MultipartBody.Part.createFormData("image", file.name, requestBody)

        val namePart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val dobPart = RequestBody.create("text/plain".toMediaTypeOrNull(), dob)
        val genderPart = RequestBody.create("text/plain".toMediaTypeOrNull(), gender)

        RetrofitClient.api.uploadStudent(namePart, dobPart, genderPart, multipart)
            .enqueue(object : Callback<StudentResponse> {

                override fun onResponse(
                    call: Call<StudentResponse>,
                    response: Response<StudentResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "Uploaded Successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Upload Failed: " + response.code(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: " + t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun getPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
        val path = cursor?.getString(columnIndex!!)
        cursor?.close()
        return path ?: ""
    }
}
