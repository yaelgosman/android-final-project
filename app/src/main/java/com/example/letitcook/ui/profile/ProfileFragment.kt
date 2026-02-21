package com.example.letitcook.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letitcook.R
import com.example.letitcook.data.AuthRepository
import com.example.letitcook.data.PostRepository
import com.example.letitcook.databinding.FragmentProfileBinding
import com.example.letitcook.models.entity.Post
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = PostRepository(requireContext())

        // Initialize ViewModel with Factory
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        setupRecyclerView()

        // Get the current user
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // Set the user's name
            binding.tvProfileName.text = user.displayName?.uppercase() ?: "No Name" //check if maybe just insert their email instead?
            binding.tvHeaderName.text = user.displayName ?: "User"

            // Set the pfp
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl)
                    .fit().centerCrop()
                    .placeholder(R.drawable.ic_person) // creates a dummy placeholder
                    .into(binding.ivProfileImage)
            } else {
                // If no image, sets a default pfp for the user
                binding.ivProfileImage.setImageResource(R.drawable.ic_person)
            }

            viewModel.loadUserReviews(user.uid)
        }

        // Setup the Settings (Gear) Icon
        binding.ivSettings.setOnClickListener { view ->
            showSettingsBottomSheet()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userReviews.collect { reviews ->
                    reviewAdapter.submitList(reviews)
                    // Update review count text
                     binding.tvReviewsCount.text = reviews.size.toString()
                }
            }
        }
    }

    // Handles the Open Settings menu on user click
    private fun showSettingsBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_settings, null)
        dialog.setContentView(view)

        // Handle "Edit Profile" Click
        view.findViewById<View>(R.id.btn_bs_edit).setOnClickListener {
            dialog.dismiss() // Closes the settings menu
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Handle "Log Out" Click
        view.findViewById<View>(R.id.btn_bs_logout).setOnClickListener {
            dialog.dismiss()
            performLogout() // Calls the logout function
        }

        dialog.show()
    }

    private fun performLogout() {
        val authRepository = AuthRepository(requireContext())
        authRepository.logout()

        // Clear back stack and navigate to Login
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()

        findNavController().navigate(R.id.loginFragment, null, navOptions)
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter { post, action ->
            when (action) {
                PostAction.EDIT -> performEdit(post)
                PostAction.DELETE -> showDeleteConfirmation(post)
            }
        }
        binding.rvUserReviews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewAdapter
        }
    }

    private fun showDeleteConfirmation(post: Post) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Review")
            .setMessage("Are you sure you want to delete this review?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePost(post)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performEdit(post: Post) {
        val bundle = Bundle().apply {
            putParcelable("post", post)
        }

        findNavController().navigate(R.id.action_profileFragment_to_addPostFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}