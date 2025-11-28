package com.example.task01

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task01.adapters.StudentAdapter
import com.example.task01.api.RetrofitClient
import com.example.task01.databinding.ActivityShowStudentBinding
import com.example.task01.dataclasses.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowStudentBinding
    private val studentList = mutableListOf<Student>()
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchStudents()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(studentList)
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter
    }

    private fun fetchStudents() {
        RetrofitClient.api.getAllStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        studentList.clear()
                        studentList.addAll(it)
                        adapter.notifyDataSetChanged()
                    } ?: Toast.makeText(this@ShowStudentActivity, "No students found", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ShowStudentActivity, "Failed to load students", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Toast.makeText(this@ShowStudentActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
