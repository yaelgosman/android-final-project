package com.example.letitcook.ui.addReview

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.letitcook.BuildConfig
import com.example.letitcook.R
import com.example.letitcook.data.PostRepository
import com.example.letitcook.databinding.FragmentAddReviewBinding
import com.example.letitcook.models.entity.Post
import com.example.letitcook.network.YelpClient
import com.example.letitcook.utils.ImageUtils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddReviewFragment : Fragment(R.layout.fragment_add_review) {

    private val YELP_API_KEY = BuildConfig.YELP_API_KEY
    private lateinit var binding: FragmentAddReviewBinding

    private val args: AddReviewFragmentArgs by navArgs()

    private var postToEdit: Post? = null
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
        val repository = PostRepository(requireContext())

        postToEdit = args.post

        if (postToEdit != null) {
            setupEditMode(postToEdit!!)
        } else {
            binding.btnPost.text = "Post"
        }

        // Fetch Restaurants
        fetchRestaurants("San Francisco")

        setupListeners(repository)
    }

    private fun setupEditMode(post: Post) {
        binding.autoCompleteRestaurant.setText(post.location)
        binding.etDescription.setText(post.description)
        binding.ratingBar.rating = post.rating
        binding.btnPost.text = "Update"

        if (!post.postImageUrl.isNullOrEmpty()) {
            binding.iconCamera.visibility = View.GONE
            binding.tvSnapLabel.visibility = View.GONE
            binding.ivSelectedImage.visibility = View.VISIBLE

            Picasso.get()
                .load(post.postImageUrl)
                .fit().centerCrop()
                .into(binding.ivSelectedImage)
        }
    }

    private fun setupListeners(repository: PostRepository) {
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.viewClickOverlay.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.ivSelectedImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnPost.setOnClickListener {
            val selectedRestaurant = binding.autoCompleteRestaurant.text.toString()
            val rating = binding.ratingBar.rating
            val text = binding.etDescription.text.toString()

            if (selectedRestaurant.isBlank()) {
                binding.tilRestaurant.error = "Please choose a place!"
                return@setOnClickListener
            }

            // Show spinner and disables ALL UI inputs
            binding.progressBarMain.visibility = View.VISIBLE
            binding.btnCancel.isEnabled = false
            binding.autoCompleteRestaurant.isEnabled = false
            binding.etDescription.isEnabled = false
            binding.viewClickOverlay.isEnabled = false // disables image upload
            binding.ratingBar.setIsIndicator(true)     // disables rating bar

            binding.btnPost.isEnabled = false
            binding.btnPost.text = if (postToEdit != null) "UPDATING..." else "COOKING..."

            lifecycleScope.launch(Dispatchers.IO) {
                val result = if (postToEdit == null) {
                    repository.addPost(
                        location = selectedRestaurant,
                        description = text,
                        rating = rating,
                        imageUri = selectedImageUri
                    )
                } else {
                    val updatedPost = postToEdit!!.copy(
                        location = selectedRestaurant,
                        description = text,
                        rating = rating
                    )
                    repository.updatePost(updatedPost, selectedImageUri)
                }

                withContext(Dispatchers.Main) {

                    // Hides spinner and enable UI inputs
                    binding.progressBarMain.visibility = View.GONE
                    binding.btnCancel.isEnabled = true
                    binding.autoCompleteRestaurant.isEnabled = true
                    binding.etDescription.isEnabled = true
                    binding.viewClickOverlay.isEnabled = true
                    binding.ratingBar.setIsIndicator(false)

                    binding.btnPost.isEnabled = true
                    if (result.success) {
                        Toast.makeText(context, "Review posted successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Error: ${result.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun fetchRestaurants(location: String) {
        // Show the Loading Indicator below the field
        binding.layoutLoadingRestaurants.visibility = View.VISIBLE

        // Set a temporary "Loading..." item in the list itself
        // This ensures if they click the dropdown immediately, it's not empty
        val loadingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listOf("Loading..."))
        binding.autoCompleteRestaurant.setAdapter(loadingAdapter)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("YELP", "Fetching restaurants for: $location")

                val response = YelpClient.apiService.searchRestaurants(
                    authHeader = "Bearer $YELP_API_KEY",
                    searchTerm = "food",
                    location = location
                )

                val restaurantNames = response.restaurants.map { it.name }
                Log.d("YELP", "Found ${restaurantNames.size} restaurants")

                withContext(Dispatchers.Main) {
                    // Hide Loading Indicator
                    binding.layoutLoadingRestaurants.visibility = View.GONE

                    if (context != null) {
                        // Set the REAL data
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
                withContext(Dispatchers.Main) {
                    binding.layoutLoadingRestaurants.visibility = View.GONE
                }
            }
        }
    }

    private fun updateImageUI(uri: Uri) {
        binding.iconCamera.visibility = View.GONE
        binding.tvSnapLabel.visibility = View.GONE
        binding.ivSelectedImage.visibility = View.VISIBLE
        val rotation = ImageUtils.getRotationAngle(requireContext(), uri)
        Picasso.get().load(uri).rotate(rotation).fit().centerCrop().into(binding.ivSelectedImage)
    }
}