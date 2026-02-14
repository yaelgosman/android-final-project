package com.example.letitcook.ui.addPost

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letitcook.R
import com.example.letitcook.databinding.FragmentAddPostBinding
import com.example.letitcook.ui.add.AddPostViewModel
import androidx.fragment.app.viewModels
import com.example.letitcook.data.FakeRepository


class AddPostFragment : Fragment(R.layout.fragment_add_post) {

    private lateinit var binding: FragmentAddPostBinding
    private val viewModel: AddPostViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivSelectedImage.setImageURI(uri)
            binding.ivSelectedImage.visibility = View.VISIBLE
            binding.iconCamera.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddPostBinding.bind(view)

        setupSpinner()

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnPost.setOnClickListener {
            val restaurant = binding.autoCompleteRestaurant.text.toString()
            val description = binding.etDescription.text.toString()
            val rating = binding.ratingBar.rating

            if (restaurant.isEmpty()) {
                binding.tilRestaurant.error = "Please choose a place"
                return@setOnClickListener
            }

            binding.btnPost.isEnabled = false

            viewModel.addPost(restaurant, description, rating, selectedImageUri) { success ->
                binding.btnPost.isEnabled = true
                if (success) {
                    Toast.makeText(context, "Post cooked successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Failed to upload post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSpinner() {
        // בינתיים זה בסדר לקחת שמות מה-FakeRepository עד שיהיה API
        val restaurantNames = FakeRepository.getRestaurantNames()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, restaurantNames)
        binding.autoCompleteRestaurant.setAdapter(adapter)
    }
}