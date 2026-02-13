package com.example.letitcook.ui.addReview

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letitcook.R
import com.example.letitcook.data.FakeRepository
import com.example.letitcook.databinding.FragmentAddReviewBinding

class AddReviewFragment : Fragment(R.layout.fragment_add_review) {

    private lateinit var binding: FragmentAddReviewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddReviewBinding.bind(view)

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPost.setOnClickListener {
            // כאן נאסוף את הנתונים
            val selectedRestaurant = binding.autoCompleteRestaurant.text.toString()
            val rating = binding.ratingBar.rating
            val text = binding.etDescription.text.toString()

            // שמירה ב-FakeRepository (ובעתיד לפיירבייס)
            // FakeRepository.addPost(....)
            //viewModel.addPost
            FakeRepository.addPost(
                userName = "You",
                location = binding.tilRestaurant.editText?.text.toString(),
                description = binding.etDescription.text.toString(),
                rating = binding.ratingBar.rating
            )
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
}