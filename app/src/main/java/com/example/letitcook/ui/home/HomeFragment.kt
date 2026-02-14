package com.example.letitcook.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letitcook.R
import com.example.letitcook.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val adapter = PostsAdapter()

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.postsRecyclerView.setHasFixedSize(true)
        binding.postsRecyclerView.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            // ברגע ש-Room מתעדכן (כי הגיע מידע מפיירבייס או שנשמר פוסט חדש), הקוד הזה רץ
            adapter.setPosts(posts)

            // טיפול במצב ריק (אופציונלי)
            if (posts.isEmpty()) {
                // binding.tvEmptyState.visibility = View.VISIBLE
            } else {
                // binding.tvEmptyState.visibility = View.GONE
            }
        }
    }
}