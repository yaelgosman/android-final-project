package com.example.letitcook.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.letitcook.R
import com.example.letitcook.data.AuthRepository
import com.example.letitcook.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        }

        // Setup the Settings (Gear) Icon
        binding.ivSettings.setOnClickListener { view ->
            showSettingsMenu(view)
        }

        // Setup Edit Profile Button
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile Clicked", Toast.LENGTH_SHORT).show()
            // TODO Navigate to EditProfileFragment in the future
        }
    }

    private fun showSettingsMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        // Inflate the menu resources:
        popup.menu.add(0, 1, 0, "Settings")
        popup.menu.add(0, 2, 0, "Log Out")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    // Handle Settings click
                    true
                }
                2 -> {
                    performLogout()
                    true
                }
                else -> false
            }
        }
        popup.show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}