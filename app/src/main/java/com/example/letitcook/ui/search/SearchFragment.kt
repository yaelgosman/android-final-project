package com.example.letitcook.ui.search

import android.graphics.Color
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
import com.google.android.material.button.MaterialButton

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

        adapter = SearchAdapter(emptyList()) { selectedRestaurant ->
            val bottomSheet = RestaurantDetailsBottomSheet(selectedRestaurant)
            bottomSheet.show(parentFragmentManager, "RestaurantDetails")
        }

        binding.rvRestaurants.adapter = adapter
    }

    private fun setupLiveSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When typing manually, we might want to reset buttons or leave them as is.
                // For now, we just search.
                viewModel.onSearchQueryChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCategories() {
        // 1. Define the list of all category buttons
        val buttons = listOf(
            binding.btnCatAll,
            binding.btnCatItalian,
            binding.btnCatAsian,
            binding.btnCatBurgers
        )

        // 2. Define colors
        val activeBgColor = android.graphics.Color.parseColor("#1A237E") // Dark Blue
        val activeTextColor = android.graphics.Color.WHITE

        val inactiveBgColor = android.graphics.Color.WHITE
        val inactiveTextColor = android.graphics.Color.parseColor("#1A237E") // Dark Blue
        val inactiveStrokeColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E0E0E0")) // Light Gray

        // 3. Helper function to update visuals
        fun updateButtonVisuals(clickedBtn: com.google.android.material.button.MaterialButton) {
            buttons.forEach { btn ->
                if (btn.id == clickedBtn.id) {
                    // === ACTIVE STATE ===
                    // Make it filled Blue
                    btn.setBackgroundColor(activeBgColor)
                    btn.setTextColor(activeTextColor)
                    btn.strokeWidth = 0 // Remove outline
                } else {
                    // === INACTIVE STATE ===
                    // Make it Transparent with Outline
                    btn.setBackgroundColor(inactiveBgColor)
                    btn.setTextColor(inactiveTextColor)
                    btn.strokeColor = inactiveStrokeColor
                    btn.strokeWidth = 3 // Thickness of the outline (approx 1dp)
                }
            }
        }

        // 4. Click Logic
        val onCategoryClick = { btn: com.google.android.material.button.MaterialButton, query: String ->
            // Update the UI immediately
            updateButtonVisuals(btn)

            // Perform the search
            binding.etSearch.setText(query)
            binding.etSearch.setSelection(query.length)
            viewModel.searchCategory(query)
        }

        // 5. Attach Listeners
        binding.btnCatAll.setOnClickListener { onCategoryClick(binding.btnCatAll, "") }
        binding.btnCatItalian.setOnClickListener { onCategoryClick(binding.btnCatItalian, "Italian") }
        binding.btnCatAsian.setOnClickListener { onCategoryClick(binding.btnCatAsian, "Asian") }
        binding.btnCatBurgers.setOnClickListener { onCategoryClick(binding.btnCatBurgers, "Burger") }
    }

    private fun observeViewModel() {
        viewModel.restaurants.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)

            val currentQuery = binding.etSearch.text.toString()
            if (currentQuery.isEmpty()) {
                binding.tvTrending.text = "TRENDING NEARBY"
            } else {
                binding.tvTrending.text = "RESULTS FOR \"${currentQuery.uppercase()}\""
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvRestaurants.alpha = 0.5f
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvRestaurants.alpha = 1.0f
            }
        }
    }
}