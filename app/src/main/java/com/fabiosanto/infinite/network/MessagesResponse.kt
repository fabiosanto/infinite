package com.fabiosanto.infinite.network

data class MessagesResponse(
    val count: Int,
    val messages: List<MessageData>,
    val pageToken: String
)

data class MessageData(
    val author: Author,
    val content: String,
    val id: Int,
    val updated: String
)

data class Author(
    val name: String,
    val photoUrl: String
)