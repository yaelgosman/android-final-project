package com.example.letitcook.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.letitcook.R
import com.example.letitcook.repositories.AuthRepository
import com.example.letitcook.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    private var profileImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            profileImageUri = uri

            // Clear filters & visibility
            binding.ivProfilePreview.visibility = View.VISIBLE
            binding.ivProfilePreview.clearColorFilter()
            binding.ivProfilePreview.setBackgroundColor(0)

            // Calculate the rotation using your Utils
            val rotationDegrees = com.example.letitcook.utils.ImageUtils.getRotationAngle(requireContext(), uri)

            // Use Picasso for the preview (handles scaling properly)
            // This resizes the huge camera image to fit to the small 100dp circle
            com.squareup.picasso.Picasso.get()
                .load(uri)
                .rotate(rotationDegrees)
                .fit()
                .centerCrop()
                .into(binding.ivProfilePreview, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        // Success
                    }
                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                    }
                })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Initialize ViewModel using your existing factory pattern
        val factory = AuthViewModelFactory(AuthRepository(requireContext()))
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        val pickImageAction = {
            imagePickerLauncher.launch("image/*")
        }

        // To make user experience smoother sets the image click listener to BOTH icons
        binding.btnUploadImage.setOnClickListener { pickImageAction() }
        binding.ivProfilePreview.setOnClickListener { pickImageAction() }

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Shows progress bar and disable button - so the user wont be able to spam click it
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
                binding.btnRegister.alpha = 0.5f

                // Disable text inputs and image upload buttons when loader is active
                binding.etFullName.isEnabled = false
                binding.etEmail.isEnabled = false
                binding.etPassword.isEnabled = false
                binding.btnUploadImage.isEnabled = false
                binding.ivProfilePreview.isEnabled = false
                binding.tvLogin.isEnabled = false

                // Calls the register function in your ViewModel
                authViewModel.register(email, password, name, profileImageUri)
            }
        }

        binding.tvLogin.setOnClickListener {
            // Navigate back to Login
            parentFragmentManager.popBackStack()
        }

        // Observe the registration result
        authViewModel.registrationResult.observe(viewLifecycleOwner) { result ->

            // Hide progress bar and re-enable button - whether navigates to the login page on successful register, or let the user fix their input.
            binding.progressBar.visibility = View.GONE
            binding.btnRegister.isEnabled = true
            binding.btnRegister.alpha = 1.0f

            // Re-enable text inputs and image upload buttons after loader finish
            binding.etFullName.isEnabled = true
            binding.etEmail.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.btnUploadImage.isEnabled = true
            binding.ivProfilePreview.isEnabled = true
            binding.tvLogin.isEnabled = true

            if (result.success) {
                Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                // Navigate to Login or Home after successful registration
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), result.errorMessage ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}