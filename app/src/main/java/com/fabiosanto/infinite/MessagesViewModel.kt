package com.fabiosanto.infinite

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fabiosanto.infinite.network.MessageData
import com.fabiosanto.infinite.network.Repo
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MessagesViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext = Dispatchers.IO + Job()

    private val RELATIVE_TIME_THRESHOLD = 10 * DateUtils.HOUR_IN_MILLIS

    private val repo = Repo()
    private val rawDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    private val readableDateFormatter =
        SimpleDateFormat("h:mm a '•' d MMM yyyy", Locale.getDefault())
    private val nowMillis = System.currentTimeMillis()

    private val items by lazy {
        MutableLiveData<List<Item>>()
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
        var list = items.value ?: arrayListOf()

        if (pageToken == null)
            list = arrayListOf()

        val newList = arrayListOf<Item>()

        try {
            val resultPair = repo.getMessages(pageToken)
            val messages = resultPair.second
            val newPageToken = resultPair.first

            list.filterTo(newList, { it is Item.Message })
            messages.mapTo(newList, { getMessageItem(it) })

            if (messages.isNotEmpty())                          //if no more messages don't add a loading footer
                newList.add(Item.LoadingFooter(newPageToken))

            items.postValue(newList)
            viewState.postValue(ViewState.READY)

        } catch (e: Exception) {
            e.printStackTrace()
            if (list.isNotEmpty() && pageToken != null) {
                list.filterTo(newList, { it is Item.Message })
                newList.add(Item.LoadingErrorCard(pageToken))
                items.postValue(newList)
            } else {
                viewState.postValue(ViewState.ERROR)
            }
        }
    }

    private fun getMessageItem(data: MessageData): Item.Message {
        val authorPhoto = Repo.BASE_URL + data.author.photoUrl
        val timeMillis = rawDateFormatter.parse(data.updated)?.time ?: nowMillis
        val timeString = getFormattedTime(timeMillis)
        return Item.Message(
            data.id,
            data.author.name,
            authorPhoto,
            data.content,
            timeString
        )
    }

    private fun getFormattedTime(timeMillis: Long): String {
        if (System.currentTimeMillis() - timeMillis > RELATIVE_TIME_THRESHOLD) {
            return readableDateFormatter.format(Date(timeMillis))
        } else {
            return DateUtils.getRelativeTimeSpanString(
                timeMillis,
                nowMillis,
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
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
    class Message(
        val id: Int,
        val authorName: String,
        val authorPhotoUrl: String,
        val content: String,
        val time: String
    ) : Item()

    class LoadingFooter(val pageToken: String) : Item()
    class LoadingErrorCard(val pageToken: String) : Item()
    object LoadingErrorPage : Item()
}

enum class ViewState {
    READY, ERROR, LOADING
}