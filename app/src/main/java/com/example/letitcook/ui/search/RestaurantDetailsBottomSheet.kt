package com.example.letitcook.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.letitcook.repositories.PostRepository
import com.example.letitcook.databinding.BottomSheetRestaurantBinding
import com.example.letitcook.models.YelpRestaurant
import com.example.letitcook.models.entity.Post
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class RestaurantDetailsBottomSheet(
    private val restaurant: YelpRestaurant
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetRestaurantBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind Data
        binding.tvResName.text = restaurant.name
        binding.tvResAddress.text = restaurant.location?.displayAddress()

        binding.rbResRating.rating = restaurant.rating.toFloat()
        binding.tvResRatingNum.text = restaurant.rating.toString()

        if (!restaurant.imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(restaurant.imageUrl).centerCrop().into(binding.ivResImage)
        }

        binding.btnSaveToCookbook.setOnClickListener {
            saveToCookbook()
        }
    }

    private fun saveToCookbook() {
        val repository = PostRepository(requireContext())

        // Convert Yelp Restaurant to your internal Post object so it can be saved in Room
        val post = Post(
            id = restaurant.id, // Use Yelp ID
            userName = restaurant.name, // We use userName field for Restaurant Name in your app logic
            location = restaurant.location?.city ?: "",
            postImageUrl = restaurant.imageUrl,
            description = "Saved from Search", // Placeholder
            rating = restaurant.rating.toFloat(),
            isSaved = false
        )

        lifecycleScope.launch {
            repository.toggleSave(post)

            Toast.makeText(context, "Saved to your Cookbook!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}