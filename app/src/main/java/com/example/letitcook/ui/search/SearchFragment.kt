package com.example.letitcook.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letitcook.R
import com.example.letitcook.databinding.FragmentSearchBinding

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        setupRecyclerView()
        setupLiveSearch()
        setupCategories()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvRestaurants.layoutManager = LinearLayoutManager(context)

        // Pass the click function
        adapter = SearchAdapter(emptyList()) { selectedRestaurant ->
            val bottomSheet = RestaurantDetailsBottomSheet(selectedRestaurant)
            bottomSheet.show(parentFragmentManager, "RestaurantDetails")
        }

        binding.rvRestaurants.adapter = adapter
    }

    private fun setupLiveSearch() {
        // This listens to every character typed
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Pass text to ViewModel immediately
                // ViewModel handles the delay (waiting for user to stop typing)
                viewModel.onSearchQueryChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCategories() {
        // Helper to handle clicks
        val onCategoryClick = { query: String ->
            binding.etSearch.setText(query) // Optional: Update text box
            binding.etSearch.setSelection(query.length) // Move cursor to end
            viewModel.searchCategory(query)
        }

        binding.btnCatAll.setOnClickListener { onCategoryClick("Restaurant") }
        binding.btnCatItalian.setOnClickListener { onCategoryClick("Italian") }
        binding.btnCatAsian.setOnClickListener { onCategoryClick("Asian") }
        binding.btnCatBurgers.setOnClickListener { onCategoryClick("Burger") }
    }

    private fun observeViewModel() {
        // 1. Update List
        viewModel.restaurants.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)

            // Update the title dynamically
            val currentQuery = binding.etSearch.text.toString()
            if (currentQuery.isEmpty()) {
                binding.tvTrending.text = "TRENDING NEARBY"
            } else {
                binding.tvTrending.text = "RESULTS FOR \"${currentQuery.uppercase()}\""
            }
        }

        // 2. Show/Hide Loading Spinner
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvRestaurants.alpha = 0.5f // Fade list slightly
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvRestaurants.alpha = 1.0f // Restore list
            }
        }
    }
}