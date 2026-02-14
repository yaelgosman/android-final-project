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

        // Setup the Settings (Gear) Icon
        binding.ivSettings.setOnClickListener { view ->
            showSettingsMenu(view)
        }

        // Setup Edit Profile Button
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to EditProfileFragment if you have one
        }
    }

    private fun showSettingsMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        // Inflate a menu resource if you have one, or add explicitly like this:
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