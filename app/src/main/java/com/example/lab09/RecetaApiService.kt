package com.example.lab09

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RecetaApiService {
    @GET("recipes")
    suspend fun getRecetas(): RecetasResponse

    @GET("recipes/{id}")
    suspend fun getRecetaById(@Path("id") id: Int): RecetaModel
}

object RetrofitRecetas {
    val servicio: RecetaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecetaApiService::class.java)
    }
}
