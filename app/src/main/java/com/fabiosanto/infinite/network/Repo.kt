package com.fabiosanto.infinite.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Repo {
    private var retrofit = Retrofit.Builder()
        .baseUrl("http://message-list.appspot.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private var service = retrofit.create(MessagesService::class.java)

    fun getMessages(token: String?): List<MessageData> {

        val response = service.messages().execute()
        return response.body()?.messages ?: listOf()
    }
}