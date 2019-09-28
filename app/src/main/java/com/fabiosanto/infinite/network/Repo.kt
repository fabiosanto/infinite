package com.fabiosanto.infinite.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Repo {
    companion object {
        const val BASE_URL = "https://message-list.appspot.com/"
    }

    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    private var service = retrofit.create(MessagesService::class.java)

    suspend fun getMessages(pageToken: String?): Pair<String, List<MessageData>> {

        val response = service.messages(pageToken).await()
        return Pair(response.pageToken, response.messages)
    }
}