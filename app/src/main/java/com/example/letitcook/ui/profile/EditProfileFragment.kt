package com.example.letitcook.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letitcook.R
import com.example.letitcook.repositories.AuthRepository
import com.example.letitcook.databinding.FragmentEditProfileBinding
import com.example.letitcook.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var newImageUri: Uri? = null // Null means "Don't change the image"

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            newImageUri = uri

            val rotation = ImageUtils.getRotationAngle(requireContext(), uri)

            Picasso.get()
                .load(uri)
                .rotate(rotation)
                .fit()
                .centerCrop()
                .into(binding.ivEditProfileImage)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)

        val user = FirebaseAuth.getInstance().currentUser
        val repository = AuthRepository(requireContext())

        // Pre-fill the existing user data
        user?.let {
            binding.etEditName.setText(it.displayName)
            if (it.photoUrl != null) {
                Picasso.get()
                    .load(it.photoUrl)
                    .fit()
                    .centerCrop()
                    .into(binding.ivEditProfileImage)
            }
        }

        // Change Photo
        binding.layoutImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Save Changes
        binding.btnSave.setOnClickListener {
            val newName = binding.etEditName.text.toString()
            if (newName.isBlank()) {
                binding.tilName.error = "Name cannot be empty"
                return@setOnClickListener
            }

            // Show Loading
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled = false

            // Run update in background
            CoroutineScope(Dispatchers.IO).launch {
                val result = repository.updateUserProfile(newName, newImageUri)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true

                    if (result.success) {
                        Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                        // Go back to Profile Page
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, result.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}