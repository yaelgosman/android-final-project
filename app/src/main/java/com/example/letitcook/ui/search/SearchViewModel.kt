package com.example.letitcook.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letitcook.BuildConfig
import com.example.letitcook.models.YelpRestaurant
import com.example.letitcook.network.YelpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _restaurants = MutableLiveData<List<YelpRestaurant>>()
    val restaurants: LiveData<List<YelpRestaurant>> = _restaurants

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val API_KEY = BuildConfig.YELP_API_KEY
    private val DEFAULT_LOCATION = "San Francisco"
    private var searchJob: Job? = null

    init {
        searchYelp("Restaurant", DEFAULT_LOCATION, 0)
    }

    // Instant Search (for Categories)
    fun searchCategory(category: String) {
        // Cancel any typing search
        searchJob?.cancel()
        searchYelp(category, DEFAULT_LOCATION, 0) // 0 delay
    }

    // Typing Search (with Debounce)
    fun onSearchQueryChanged(query: String) {
        // Cancel previous job if user is still typing
        searchJob?.cancel()

        if (query.isBlank()) {
            searchYelp("Restaurant", DEFAULT_LOCATION, 0)
            return
        }

        // Start new job
        searchJob = viewModelScope.launch {
            // Wait 500ms to see if user types more
            delay(500)

            if (query.isNotEmpty()) {
                searchYelp(query, DEFAULT_LOCATION, 0)
            }
        }
    }

    private fun searchYelp(term: String, location: String, delayTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true) // Show Spinner

            try {
                val response = YelpClient.apiService.searchRestaurants(
                    authHeader = "Bearer $API_KEY",
                    searchTerm = term,
                    location = location
                )

                _restaurants.postValue(response.restaurants)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SearchViewModel", "Error: ${e.message}")
                _restaurants.postValue(emptyList())
            } finally {
                _isLoading.postValue(false) // Hide Spinner
            }
        }
    }
}