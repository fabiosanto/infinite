package com.fabiosanto.infinite.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface MessagesService {
    @GET("messages")
    fun messages(@Query("pageToken") pageToken: String?,
                 @Query("limit") limit: Int = 10): Deferred<MessagesResponse>
}

