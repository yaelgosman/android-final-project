package com.example.letitcook.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.letitcook.MainActivity
import com.example.letitcook.R
import com.example.letitcook.data.repository.AuthRepository

class AuthenticationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository.instance

        if (authRepository.isUserLoggedIn()) {
            navigateToMainActivity()
        } else {
            setContentView(R.layout.activity_authentication)
            supportFragmentManager.beginTransaction().replace(R.id.auth_container, LoginFragment()).commit()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}