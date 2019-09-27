package com.fabiosanto.infinite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fabiosanto.infinite.network.MessageData
import com.fabiosanto.infinite.network.Repo
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MessagesViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext = Dispatchers.IO + Job()

    private val repo = Repo()

    private val items by lazy {
        MutableLiveData<List<Item>>().apply {
            postValue(arrayListOf(Item.LoadingMessage, Item.LoadingMessage, Item.LoadingMessage))
        }
    }

    val itemsObservable: LiveData<List<Item>>
        get() = items

    init {
        loadMore(null)
    }

    fun loadMore(pageToken: String?) = launch {
        val list = items.value ?: arrayListOf()
        val newList = arrayListOf<Item>()
        try {
            val resultPair = repo.getMessages(pageToken)
            val messages = resultPair.second
            val newPageToken = resultPair.first

            list.filterTo(newList, { it is Item.Message }) //tobe improved!
            messages.mapTo(newList, { Item.Message(it) })
            newList.add(Item.LoadingFooter(newPageToken))

            items.postValue(newList)
        } catch (e: Exception) {

            if (list.isNotEmpty() && pageToken != null) {
                list.filterTo(newList, { it is Item.Message })
                newList.add(Item.LoadingErrorCard(pageToken))
                items.postValue(newList)
            } else {
//                items.postValue(arrayListOf(Item.LoadingErrorPage()))
            }
        }
    }
}

sealed class Item {
    class Message(val data: MessageData) : Item()
    class LoadingFooter(val pageToken: String) : Item()
    class LoadingErrorCard(val pageToken: String) : Item()
    object LoadingErrorPage : Item()
    object LoadingMessage : Item()
}