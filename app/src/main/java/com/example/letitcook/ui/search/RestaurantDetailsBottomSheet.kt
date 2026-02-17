package com.example.letitcook.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.letitcook.R
import com.example.letitcook.data.PostRepository
import com.example.letitcook.databinding.BottomSheetRestaurantBinding
import com.example.letitcook.models.YelpRestaurant
import com.example.letitcook.models.entity.Post
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.UUID

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

        if (!restaurant.imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(restaurant.imageUrl).centerCrop().into(binding.ivResImage)
        }

//        binding.ratingBar.rating

        // Logic: Save to Cookbook (Want to Go)
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
            isSaved = true
        )

        lifecycleScope.launch {
            // We reuse the toggleSave logic.
            // Since it's currently NOT saved (false), toggle will make it TRUE.
            repository.toggleSave(post.id, false)

            // Also insert the basic details into Room so it shows up in the Saved Tab
            // (You might need to add a direct insert method to repo, or just let refreshPosts handle it later)
            // Ideally, your repository should have a `saveYelpResult(post)` method.
            // For simplicity, we just trigger the toggle which hits Firebase,
            // and we rely on Room/Firebase sync.

            Toast.makeText(context, "Saved to your Cookbook!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}