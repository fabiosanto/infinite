package com.fabiosanto.infinite

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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

        messagesAdapter = MessagesAdapter({ viewModel.loadMore(it) }, { viewModel.loadMore(it) })
        recyclerView.addItemDecoration(VerticalSpace())
        recyclerView.adapter = messagesAdapter

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

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

    private val swipeCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            viewModel.itemDismissed(viewHolder.adapterPosition)
            showSnackbarMessageDismissed()
        }
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
