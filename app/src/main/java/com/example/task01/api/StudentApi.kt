package com.example.task01.api

import com.example.task01.dataclasses.Student
import com.example.task01.dataclasses.StudentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface StudentApi {

    @Multipart
    @POST("api/students/add")
    fun uploadStudent(
        @Part("name") name: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<StudentResponse>

    @GET("api/students/all")
    fun getAllStudents(): Call<List<Student>>
}
