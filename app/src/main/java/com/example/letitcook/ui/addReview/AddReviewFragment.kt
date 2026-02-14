package com.example.letitcook.ui.addReview

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letitcook.R
import com.example.letitcook.data.FakeRepository
import com.example.letitcook.databinding.FragmentAddReviewBinding
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.letitcook.BuildConfig
import com.example.letitcook.network.YelpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.squareup.picasso.Picasso
import com.example.letitcook.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth


class AddReviewFragment : Fragment(R.layout.fragment_add_review) {

    private val YELP_API_KEY = BuildConfig.YELP_API_KEY
    private lateinit var binding: FragmentAddReviewBinding

    // Variable to store the selected image for later uploading
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            updateImageUI(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddReviewBinding.bind(view)

        // Fetch Restaurants from yelp instead of Fake Repository
        fetchRestaurants("New York") // for now its new york because i dont think there are and israeli location restaurants in yelp...

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.viewClickOverlay.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnPost.setOnClickListener {
            // כאן נאסוף את הנתונים
            val selectedRestaurant = binding.autoCompleteRestaurant.text.toString()
            val rating = binding.ratingBar.rating
            val text = binding.etDescription.text.toString()

            if (selectedRestaurant.isBlank()) {
                binding.tilRestaurant.error = "Please choose a place!"
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            // For now: saves the new post to the fakeRepository vv

            // שמירה ב-FakeRepository (ובעתיד לפיירבייס)
            // FakeRepository.addPost(....)
            //viewModel.addPost
            FakeRepository.addPost(
                userName = currentUser?.displayName.toString(),
                location = binding.tilRestaurant.editText?.text.toString(),
                description = binding.etDescription.text.toString(),
                rating = binding.ratingBar.rating,
                userAvatarUrl = currentUser?.photoUrl,
                postImageUrl = selectedImageUri
            )

            Toast.makeText(context, "Review posted!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.imageContainer.setOnClickListener {
            // כאן יפתח בוחר התמונות (גלריה/מצלמה)
        }
    }

    private fun setupSpinner() {
        // 1. קבלת רשימת השמות מהריפו
        val restaurantNames = FakeRepository.getRestaurantNames()

        // 2. יצירת אדפטר פשוט לרשימה
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, restaurantNames)

        // 3. חיבור האדפטר לשדה
        binding.autoCompleteRestaurant.setAdapter(adapter)
    }

    // Fetches restaurants by location from the Yelp API
    private fun fetchRestaurants(location: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Make the Real API Call
                val response = YelpClient.apiService.searchRestaurants(
                    authHeader = "Bearer $YELP_API_KEY",
                    searchTerm = "food",
                    location = location
                )

                // Extract the restaurant names
                val restaurantNames = response.restaurants.map { it.name }

                // Updates the UI
                withContext(Dispatchers.Main) {
                    if (context != null) { // Check context to avoid crashes if user left screen
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            restaurantNames
                        )
                        binding.autoCompleteRestaurant.setAdapter(adapter)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("YELP", "Error fetching restaurants: ${e.message}")
            }
        }
    }

    // Helper Function to update the UI state
    private fun updateImageUI(uri: Uri) {
        // Hide the placeholder icon and text of the image input
        binding.iconCamera.visibility = View.GONE
        binding.tvSnapLabel.visibility = View.GONE

        // Show the ImageView
        binding.ivSelectedImage.visibility = View.VISIBLE

        // Load the image
        val rotation = ImageUtils.getRotationAngle(requireContext(), uri)

        Picasso.get()
            .load(uri)
            .rotate(rotation)
            .fit()
            .centerCrop()
            .into(binding.ivSelectedImage)
    }
}