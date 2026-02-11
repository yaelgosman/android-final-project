package com.example.letitcook.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.letitcook.MainActivity
import com.example.letitcook.R
import com.example.letitcook.data.AuthRepository
import com.example.letitcook.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint;

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

//    private val authViewModel: AuthViewModel by viewModels()
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
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.login(email, password)
            }
        }

        // Checks if the login was successful
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            if (result.success) {
                requireActivity().finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            } else {
                Toast.makeText(requireContext(), result.errorMessage ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.auth_container, RegisterFragment()).addToBackStack(null).commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//
//        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
//        val tvSignUp = view.findViewById<TextView>(R.id.tvSignUp)
//
//        btnLogin.setOnClickListener {
//            // פיקטיבי – תמיד מצליח
//            val action =
//                LoginFragmentDirections.actionLoginToHome()
//            findNavController().navigate(action)
//        }
//
//        tvSignUp.setOnClickListener {
//            val action =
//                LoginFragmentDirections.actionLoginToRegister()
//            findNavController().navigate(action)
//        }
//    }
}