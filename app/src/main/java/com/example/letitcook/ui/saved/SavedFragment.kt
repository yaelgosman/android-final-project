package com.example.letitcook.ui.saved

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letitcook.R
import com.example.letitcook.data.PostRepository
import com.example.letitcook.databinding.FragmentSavedBinding
import com.example.letitcook.models.entity.Post

class SavedFragment : Fragment(R.layout.fragment_saved) {

    private lateinit var binding: FragmentSavedBinding
    private val viewModel: SavedViewModel by viewModels {
        com.example.letitcook.ui.feed.HomeViewModelFactory(PostRepository(requireContext()))
    }

    private lateinit var adapter: SavedAdapter
    private var fullPostList: List<Post> = emptyList()
    private var currentFilterMode = 0 // 0=All, 1=Want, 2=Top

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedBinding.bind(view)

        setupRecyclerView()
        setupFilters()

        viewModel.savedPosts.observe(viewLifecycleOwner) { posts ->
            fullPostList = posts
            applyFilter()
        }
    }

    private fun setupRecyclerView() {
        adapter = SavedAdapter { post ->
            // Handle unsaving
            viewModel.toggleSave(post)
        }
        binding.rvSaved.layoutManager = LinearLayoutManager(context)
        binding.rvSaved.adapter = adapter
    }

    private fun setupFilters() {
        binding.btnFilterAll.setOnClickListener { updateFilterUI(0) }
        binding.btnFilterTop.setOnClickListener { updateFilterUI(1) }
    }

    private fun updateFilterUI(mode: Int) {
        currentFilterMode = mode

        // Reset styles
        val inactiveBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_filter_inactive)
        val activeBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_filter_active)
        val darkColor = ContextCompat.getColor(requireContext(), R.color.cookbook_text_dark)
        val whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white)

        // Helper to reset button
        fun resetButton(tv: android.widget.TextView) {
            tv.background = inactiveBg
            tv.setTextColor(darkColor)
        }

        resetButton(binding.btnFilterAll)
        resetButton(binding.btnFilterTop)

        // Set Active
        val activeBtn = when(mode) {
            1 -> binding.btnFilterTop
            else -> binding.btnFilterAll
        }
        activeBtn.background = activeBg
        activeBtn.setTextColor(whiteColor)

        applyFilter()
    }

    private fun applyFilter() {
        val filteredList = when (currentFilterMode) {
            1 -> fullPostList.filter { it.rating >= 4.5 } // Top Rated Logic
            else -> fullPostList // All Saved
        }
        adapter.setPosts(filteredList)
    }
}