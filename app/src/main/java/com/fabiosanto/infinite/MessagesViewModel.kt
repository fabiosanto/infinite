package com.fabiosanto.infinite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fabiosanto.infinite.network.MessageData
import com.fabiosanto.infinite.network.Repo
import kotlinx.coroutines.*
import java.lang.Exception

class MessagesViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext = Dispatchers.IO + Job()

    private val repo = Repo()

    private val items by lazy {
        MutableLiveData<List<Item>>().apply {
            postValue(arrayListOf())
        }
    }

    private val viewState by lazy {
        MutableLiveData<ViewState>()
    }

    val itemsObservable: LiveData<List<Item>>
        get() = items
    val viewStateObservable: LiveData<ViewState>
        get() = viewState

    private var lastDismissed: Pair<Int, Item.Message>? = null

    init {
        viewState.postValue(ViewState.LOADING)
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
            viewState.postValue(ViewState.READY)

        } catch (e: Exception) {

            if (list.isNotEmpty() && pageToken != null) {
                list.filterTo(newList, { it is Item.Message })
                newList.add(Item.LoadingErrorCard(pageToken))
                items.postValue(newList)
            } else {
                viewState.postValue(ViewState.ERROR)
            }
        }
    }

    fun itemDismissed(position: Int) {
        items.value?.let {
            val newList = arrayListOf<Item>()
            it.mapTo(newList, { item -> item })

            val item = newList[position]
            lastDismissed = Pair(position, item as Item.Message)

            newList.removeAt(position)

            items.postValue(newList)
        }
    }

    fun undoDismissal() {
        if (lastDismissed == null)
            return

        items.value?.let {
            val newList = arrayListOf<Item>()
            it.mapTo(newList, { item -> item })

            newList.add(lastDismissed!!.first, lastDismissed!!.second)
            items.postValue(newList)
        }

        lastDismissed = null
    }
}

sealed class Item {
    class Message(val data: MessageData) : Item()
    class LoadingFooter(val pageToken: String) : Item()
    class LoadingErrorCard(val pageToken: String) : Item()
    object LoadingErrorPage : Item()
}

enum class ViewState {
    READY, ERROR, LOADING
}