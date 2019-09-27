package com.fabiosanto.infinite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.fabiosanto.infinite.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loading_error_page.*

class MainActivity : AppCompatActivity() {

    private val viewModel = MessagesViewModel()
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        messagesAdapter = MessagesAdapter({ viewModel.loadMore(it) }, { viewModel.loadMore(it) })
        recyclerView.addItemDecoration(VerticalSpace())
        recyclerView.adapter = messagesAdapter

        retry.setOnClickListener {
            viewModel.loadMore(null)
        }

        viewModel.itemsObservable.observe(this, Observer {
            messagesAdapter.submitList(it)
        })
        viewModel.viewStateObservable.observe(this, Observer {
            binding.viewState = it
        })
    }
}
