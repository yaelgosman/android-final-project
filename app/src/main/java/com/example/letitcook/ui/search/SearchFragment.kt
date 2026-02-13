package com.example.letitcook.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letitcook.R
import com.example.letitcook.data.FakeRepository
import com.example.letitcook.databinding.FragmentSearchBinding
import com.example.letitcook.ui.search.SearchAdapter

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        binding.rvRestaurants.layoutManager = LinearLayoutManager(context)

        val restaurantList = FakeRepository.getRestaurants()

        binding.rvRestaurants.adapter = SearchAdapter(restaurantList)

        // דוגמה לטיפול בכפתורי סינון (אופציונלי כרגע)
        // binding.btnItalian.setOnClickListener { ... }
    }
}
