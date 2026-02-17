package com.example.letitcook.ui.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letitcook.R
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.letitcook.data.PostRepository


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var viewModel: HomeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = PostRepository(requireContext())

        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.postsRecyclerView)
        val adapter = PostsAdapter(emptyList()) { post ->
            viewModel.toggleSave(post)
        }

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.refreshPosts() // This calls the  repository again
        }

        viewModel.posts.observe(viewLifecycleOwner) { postList ->
            adapter.updatePosts(postList)
            swipeRefresh.isRefreshing = false
        }
    }
}