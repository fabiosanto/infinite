package com.fabiosanto.infinite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel = MessagesViewModel()
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messagesAdapter = MessagesAdapter { viewModel.loadMore() }
        recyclerView.adapter = messagesAdapter

        viewModel.itemsObservable.observe(this, Observer {
            messagesAdapter.submitList(it)
        })
    }
}
