package com.example.letitcook.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.letitcook.R
import com.example.letitcook.data.repository.AuthRepository
import com.example.letitcook.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    private var profileImageUri: Uri? = null
//
//    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        if (uri != null) {
//            profileImageUri = uri
//            binding.profileImageView.setImageURI(uri)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Initialize ViewModel using your existing factory pattern
        val factory = AuthViewModelFactory(AuthRepository.instance)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Calls the register function in your ViewModel
                authViewModel.register(email, password, profileImageUri)
            }
        }

        binding.tvLogin.setOnClickListener {
            // Navigate back to Login
//            findNavController().navigate(R.id.action_register_to_login) // highlighted to test a bug in navigation
            parentFragmentManager.popBackStack()
            // OR use: findNavController().navigate(R.id.action_register_to_login)
        }

        // Observe the registration result
        authViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            if (result.success) {
                Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                // Navigate to Login or Home after successful registration
//                findNavController().navigate(R.id.action_register_to_home)
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

//package com.example.letitcook.ui.auth
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.letitcook.R
//
//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [RegisterFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class RegisterFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_register, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment RegisterFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            RegisterFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}