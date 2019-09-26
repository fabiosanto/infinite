package com.fabiosanto.infinite.network

import retrofit2.Call
import retrofit2.http.GET

interface MessagesService {
    @GET("messages")
    fun messages(): Call<MessagesResponse>
}

