package com.example.task01.dataclasses

data class StudentResponse(
    val success: Boolean,
    val message: String,
    val student: Student
)