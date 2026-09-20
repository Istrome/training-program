package com.egorov.workout

import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @GET("exercises")
    fun getExercises(): Call<List<ExerciseResponse>>

    @POST("exercises")
    fun addExercise(@Body exercise: Exercise): Call<Void>

    @POST("register")
    fun register(@Body user: User): Call<Map<String, String>>

    @POST("login")
    fun login(@Body user: User): Call<Map<String, String>>

}