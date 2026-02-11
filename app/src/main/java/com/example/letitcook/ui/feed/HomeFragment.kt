package com.example.letitcook.ui.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letitcook.R
import androidx.fragment.app.viewModels

class HomeFragment : Fragment(R.layout.fragment_home) {


    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.postsRecyclerView)

        recycler.adapter = PostsAdapter(viewModel.posts)
        recycler.layoutManager = LinearLayoutManager(requireContext())
    }
}