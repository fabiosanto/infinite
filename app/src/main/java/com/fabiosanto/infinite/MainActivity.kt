package com.fabiosanto.infinite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val viewModel = MessagesViewModel()
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messagesAdapter = MessagesAdapter { pageToken -> viewModel.loadMore(pageToken) }
        recyclerView.adapter = messagesAdapter

        recyclerView.addItemDecoration(VerticalSpace())

        viewModel.itemsObservable.observe(this, Observer {
            messagesAdapter.submitList(it)
        })
    }
}
