package com.example.letitcook.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.letitcook.R

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvSignUp = view.findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            // פיקטיבי – תמיד מצליח
            val action =
                LoginFragmentDirections.actionLoginToHome()
            findNavController().navigate(action)
        }

        tvSignUp.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginToRegister()
            findNavController().navigate(action)
        }
    }
}