package com.example.letitcook.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.letitcook.R
import com.example.letitcook.repositories.AuthRepository
import com.example.letitcook.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val factory = AuthViewModelFactory(AuthRepository(requireContext()))
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                // Show the progress bar and disable the button before network request - so the user wont be able to spam it
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLogin.isEnabled = false
                binding.btnLogin.alpha = 0.5f

                // Disable the input fields when loader is active
                binding.etEmail.isEnabled = false
                binding.etPassword.isEnabled = false
                binding.tvSignUp.isEnabled = false

                authViewModel.login(email, password)
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // if login was successful
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->

            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
            binding.btnLogin.alpha = 1.0f

            // Re-enable input fields after loader finish
            binding.etEmail.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.tvSignUp.isEnabled = true

            if (result.success) {
                try {

                    findNavController().navigate(
                        R.id.action_login_to_home,
                        null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(requireContext(), result.errorMessage ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}