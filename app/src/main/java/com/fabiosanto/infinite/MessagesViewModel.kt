package com.fabiosanto.infinite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fabiosanto.infinite.network.MessageData
import com.fabiosanto.infinite.network.Repo

class MessagesViewModel : ViewModel() {

    private val repo = Repo()
    private var lastToken: String? = null

    private val items by lazy {
        MutableLiveData<List<Item>>().apply {
            postValue(arrayListOf(Item.LoadingMessage, Item.LoadingMessage, Item.LoadingMessage))
        }
    }

    val itemsObservable: LiveData<List<Item>>
        get() = items

    init {
        loadMore()
    }

    fun loadMore() {
        //todo async coroutine

        val messages = repo.getMessages(lastToken)
        val list = items.value ?: arrayListOf()

        val newList = arrayListOf<Item>()
        list.filterTo(newList, {it is Item.Message})
        messages.mapTo(newList, { Item.Message(it) })
        newList.add(Item.LoadingFooter)

        items.postValue(newList)
    }
}

sealed class Item {
    class Message(data: MessageData) : Item()
    object LoadingFooter : Item()
    object LoadingMessage : Item()
}