package com.fabiosanto.infinite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fabiosanto.infinite.custom.VerticalSpace
import com.fabiosanto.infinite.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
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
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp)

        messagesAdapter = MessagesAdapter({ viewModel.loadMore(it) },
            { viewModel.loadMore(it) },
            { viewModel.itemDismissed(it); showSnackbarMessageDismissed() })

        swipe_refresh.setColorSchemeResources(R.color.colorPrimary)
        swipe_refresh.setOnRefreshListener {
            viewModel.loadMore(null); swipe_refresh.isRefreshing = true
        }

        recyclerView.addItemDecoration(VerticalSpace())
        recyclerView.adapter = messagesAdapter

        val itemTouchHelper = ItemTouchHelper(messagesAdapter.swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        retry.setOnClickListener {
            viewModel.loadMore(null)
        }

        viewModel.itemsObservable.observe(this, Observer {
            messagesAdapter.submitList(it)
            swipe_refresh.isRefreshing = false
        })
        viewModel.viewStateObservable.observe(this, Observer {
            binding.viewState = it
        })
    }

    private fun showSnackbarMessageDismissed() {
        Snackbar.make(
            recyclerView,
            getString(R.string.dismissed_message),
            Snackbar.LENGTH_SHORT
        ).setAction(getString(R.string.undo)) {
            viewModel.undoDismissal()
        }.show()
    }
}
